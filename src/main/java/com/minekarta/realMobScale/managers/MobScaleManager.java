package com.minekarta.realMobScale.managers;

import org.bukkit.entity.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.util.Vector;
import com.minekarta.realMobScale.RealMobScale;
import com.minekarta.realMobScale.data.MobData;
import com.minekarta.realMobScale.data.ScaleProfile;
import com.minekarta.realMobScale.events.MobScaleEvent;
import com.minekarta.realMobScale.events.MobScaledEvent;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import org.bukkit.Bukkit;

public class MobScaleManager {
    private final ConfigManager configManager;
    
    public MobScaleManager() {
        this.configManager = RealMobScale.getInstance().getConfigManager();
    }
    
    /**
     * Apply realistic scaling to mob based on real-world measurements
     * @return true if scaling was successfully applied, false otherwise
     */
    public boolean applyRealisticScaling(LivingEntity entity) {
        if (!shouldScale(entity)) return false;

        ScaleProfile profile = MobData.getScaleProfile(entity.getType());
        if (profile == null) return false;

        // Calculate the scale factor with biome scaling
        double baseScaleFactor = getAdjustedScale(entity, profile);
        double biomeMultiplier = RealMobScale.getInstance().getBiomeScalingManager().getBiomeScaleMultiplier(
            entity.getLocation().getBlock().getBiome(),
            entity.getType().name()
        );
        double scaleFactor = baseScaleFactor * biomeMultiplier;

        // Fire pre-scaling event (cancellable)
        MobScaleEvent preEvent = new MobScaleEvent(entity, profile, scaleFactor);
        Bukkit.getPluginManager().callEvent(preEvent);

        if (preEvent.isCancelled()) {
            if (configManager.isDebugMode()) {
                RealMobScale.getInstance().getLogger().info("Scaling cancelled by plugin for " + entity.getType().name());
            }
            return false;
        }

        // Use potentially modified values from the event
        profile = preEvent.getProfile();
        scaleFactor = preEvent.getScaleFactor();

        // Apply visual scaling using packetevents if available (checked at runtime)
        applyVisualScaling(entity, profile, scaleFactor);

        // Apply attribute adjustments
        applyAttributeScaling(entity, profile, scaleFactor);

        // Apply hitbox adjustments
        applyHitboxScaling(entity, profile, scaleFactor);

        // Fire post-scaling event (informational)
        MobScaledEvent postEvent = new MobScaledEvent(entity, profile, scaleFactor);
        Bukkit.getPluginManager().callEvent(postEvent);

        return true;
    }
    
    private void applyVisualScaling(LivingEntity entity, ScaleProfile profile, double scaleFactor) {
        try {
            // Check if this is a baby animal and adjust scale accordingly
            float scaleFloat = (float) scaleFactor;
            String entityType = entity.getType().name();
            String ageInfo = isBabyAnimal(entity) ? "(Baby)" : "(Adult)";

            // Primary method: PacketEvents integration for smooth visual scaling
            if (RealMobScale.getInstance().getMetadataHandler() != null) {
                // Store the scale information for packet handling
                RealMobScale.getInstance().getMetadataHandler().addEntityScale(
                    entity.getUniqueId(),
                    profile,
                    scaleFloat
                );

                // Also apply server-side scale attribute for fallback and compatibility
                if (entity.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null) {
                    entity.getAttribute(org.bukkit.attribute.Attribute.SCALE).setBaseValue(scaleFloat);
                }

                RealMobScale.getInstance().getLogger().info("Visual scaling applied to " + entityType + " " + ageInfo +
                    " - " + profile.getDescription() + " (Real: " + profile.getRealWorldHeight() +
                    "m, Scale: " + scaleFactor + "x) using PacketEvents + SCALE attribute");
            } else {
                // Fallback: use only the built-in scale attribute
                if (entity.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null) {
                    entity.getAttribute(org.bukkit.attribute.Attribute.SCALE).setBaseValue(scaleFloat);
                    RealMobScale.getInstance().getLogger().info("Visual scaling applied to " + entityType + " " + ageInfo +
                        " - " + profile.getDescription() + " (Real: " + profile.getRealWorldHeight() +
                        "m, Scale: " + scaleFactor + "x) using SCALE attribute only");
                } else {
                    // Last resort: try to use older methods
                    try {
                        entity.teleport(entity.getLocation().clone().multiply(scaleFloat));
                        RealMobScale.getInstance().getLogger().warning("Visual scaling applied to " + entityType + " " + ageInfo +
                            " (scale: " + scaleFactor + ") using transformation fallback");
                    } catch (Exception fallbackException) {
                        RealMobScale.getInstance().getLogger().severe("Could not apply visual scaling to " + entityType +
                            " - All methods failed, using default size");
                    }
                }
            }
        } catch (Exception e) {
            RealMobScale.getInstance().getLogger().warning("Error applying visual scaling to " + entity.getType().name() + ": " + e.getMessage());
        }
    }

    /**
     * Get the adjusted scale factor considering baby animals
     */
    private double getAdjustedScale(LivingEntity entity, ScaleProfile profile) {
        if (isBabyAnimal(entity)) {
            // Check for custom baby scale first
            double customBabyScale = configManager.getCustomBabyScale(entity.getType());
            if (customBabyScale > 0) {
                // Use custom baby scale with global multiplier
                double babyMultiplier = configManager.getBabyScaleMultiplier();
                return profile.getScaleFactor() * customBabyScale * babyMultiplier;
            } else {
                // Use default baby scale calculation
                double babyMultiplier = configManager.getBabyScaleMultiplier();
                return profile.getBabyAdjustedScale(babyMultiplier);
            }
        }
        return profile.getScaleFactor();
    }

    /**
     * Check if the entity is a baby animal
     */
    private boolean isBabyAnimal(LivingEntity entity) {
        // Check for ageable entities (most animals)
        if (entity instanceof org.bukkit.entity.Ageable) {
            return !((org.bukkit.entity.Ageable) entity).isAdult();
        }

        // Check for specific baby entity types
        String entityName = entity.getType().name().toLowerCase();
        if (entityName.contains("baby") || entityName.contains("child")) {
            return true;
        }

        return false;
    }
    
        
    private void applyAttributeScaling(LivingEntity entity, ScaleProfile profile, double scaleFactor) {
        // Calculate size multiplier based on the actual scale factor applied
        double sizeMultiplier = scaleFactor / profile.getScaleFactor();
        String entityType = entity.getType().name();
        String ageInfo = isBabyAnimal(entity) ? "(Baby)" : "(Adult)";

        // Adjust health based on size (larger animals = more health)
        double healthMultiplier = profile.getHealthMultiplier() * sizeMultiplier;
        if (entity.getAttribute(Attribute.MAX_HEALTH) != null) {
            double baseHealth = entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
            double newHealth = baseHealth * healthMultiplier;
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newHealth);
            entity.setHealth(newHealth);

            RealMobScale.getInstance().getLogger().info("Health adjusted for " + entityType + " " + ageInfo +
                ": " + String.format("%.1f", baseHealth) + " → " + String.format("%.1f", newHealth) + " HP");
        }

        // Adjust damage based on size
        double damageMultiplier = profile.getDamageMultiplier() * sizeMultiplier;
        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
            double baseDamage = entity.getAttribute(Attribute.ATTACK_DAMAGE).getBaseValue();
            double newDamage = baseDamage * damageMultiplier;
            entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(newDamage);

            RealMobScale.getInstance().getLogger().info("Damage adjusted for " + entityType + " " + ageInfo +
                ": " + String.format("%.1f", baseDamage) + " → " + String.format("%.1f", newDamage) + " DMG");
        }

        // Adjust movement speed based on size (smaller = faster, larger = slower)
        double speedMultiplier = profile.getSpeedMultiplier();
        if (isBabyAnimal(entity)) {
            // Babies are typically more energetic and playful
            // But still proportional to their size
            speedMultiplier *= 1.15; // Slightly faster than adults, but not too much
        }

        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            double baseSpeed = entity.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue();
            double newSpeed = baseSpeed * speedMultiplier;
            entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(newSpeed);

            RealMobScale.getInstance().getLogger().info("Speed adjusted for " + entityType + " " + ageInfo +
                ": " + String.format("%.3f", baseSpeed) + " → " + String.format("%.3f", newSpeed) + " m/s");
        }
    }
    
    private void applyHitboxScaling(LivingEntity entity, ScaleProfile profile, double scaleFactor) {
        // Paper 1.21 hitbox adjustment
        // Note: Direct hitbox manipulation might require NMS in some cases
        // This is a conceptual implementation using bounding box adjustment

        // For now, this is a placeholder for future hitbox scaling implementation
        // The visual scaling via PacketEvents should handle most of the perceived size changes
        if (configManager.isDebugMode()) {
            RealMobScale.getInstance().getLogger().fine("Hitbox scaling placeholder called for " +
                entity.getType().name() + " with scale factor " + scaleFactor);
        }
    }
    
    private boolean shouldScale(LivingEntity entity) {
        // Use the new comprehensive entity checking method
        return configManager.shouldScaleEntity(entity);
    }

    /**
     * Apply scaling to all existing entities in all worlds
     * Useful for server restarts or when enabling the plugin
     */
    public void applyScalingToExistingEntities() {
        int count = 0;
        for (org.bukkit.World world : RealMobScale.getInstance().getServer().getWorlds()) {
            for (org.bukkit.entity.Entity entity : world.getLivingEntities()) {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    if (applyRealisticScaling(livingEntity)) {
                        count++;
                    }
                }
            }
        }

        if (configManager.isDebugMode()) {
            RealMobScale.getInstance().getLogger().info("Applied scaling to " + count + " existing entities");
        }
    }

    /**
     * Apply scaling to all entities in a specific world
     */
    public int applyScalingToWorld(String worldName) {
        org.bukkit.World world = RealMobScale.getInstance().getServer().getWorld(worldName);
        if (world == null) {
            return 0;
        }

        int count = 0;
        for (org.bukkit.entity.Entity entity : world.getLivingEntities()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (applyRealisticScaling(livingEntity)) {
                    count++;
                }
            }
        }

        if (configManager.isDebugMode()) {
            RealMobScale.getInstance().getLogger().info("Applied scaling to " + count + " entities in world " + worldName);
        }

        return count;
    }

    /**
     * Apply scaling to entities within a radius of a location
     * Useful for command-based scaling or specific area updates
     */
    public int applyScalingInRadius(org.bukkit.Location center, double radius) {
        if (center == null || radius <= 0) {
            return 0;
        }

        int count = 0;
        double radiusSquared = radius * radius;

        for (org.bukkit.entity.Entity entity : center.getWorld().getLivingEntities()) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (livingEntity.getLocation().distanceSquared(center) <= radiusSquared) {
                    if (applyRealisticScaling(livingEntity)) {
                        count++;
                    }
                }
            }
        }

        if (configManager.isDebugMode()) {
            RealMobScale.getInstance().getLogger().info("Applied scaling to " + count + " entities within radius " + radius +
                " of " + formatLocation(center));
        }

        return count;
    }

    /**
     * Format location for logging
     */
    private String formatLocation(org.bukkit.Location loc) {
        return String.format("(%.1f, %.1f, %.1f)", loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Get statistics about current scaling
     */
    public ScalingStatistics getScalingStatistics() {
        int totalEntities = 0;
        int scaledEntities = 0;
        int worldsChecked = 0;

        for (org.bukkit.World world : RealMobScale.getInstance().getServer().getWorlds()) {
            if (!configManager.isWorldEnabled(world.getName())) {
                continue;
            }

            worldsChecked++;
            for (org.bukkit.entity.Entity entity : world.getLivingEntities()) {
                if (entity instanceof LivingEntity) {
                    totalEntities++;
                    LivingEntity livingEntity = (LivingEntity) entity;

                    // Check if entity is scaled
                    try {
                        if (livingEntity.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null &&
                            livingEntity.getAttribute(org.bukkit.attribute.Attribute.SCALE).getBaseValue() != 1.0) {
                            scaledEntities++;
                        }
                    } catch (Exception e) {
                        // Ignore errors in statistics
                    }
                }
            }
        }

        return new ScalingStatistics(totalEntities, scaledEntities, worldsChecked);
    }

    /**
     * Statistics class for scaling information
     */
    public static class ScalingStatistics {
        private final int totalEntities;
        private final int scaledEntities;
        private final int worldsChecked;

        public ScalingStatistics(int totalEntities, int scaledEntities, int worldsChecked) {
            this.totalEntities = totalEntities;
            this.scaledEntities = scaledEntities;
            this.worldsChecked = worldsChecked;
        }

        public int getTotalEntities() { return totalEntities; }
        public int getScaledEntities() { return scaledEntities; }
        public int getWorldsChecked() { return worldsChecked; }
        public double getScalingPercentage() {
            return totalEntities > 0 ? (double) scaledEntities / totalEntities * 100 : 0;
        }
    }
}