package com.minekarta.realMobScale.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import com.minekarta.realMobScale.RealMobScale;

/**
 * Comprehensive event listener for all mob spawning scenarios
 * Handles natural spawns, spawner spawns, breeding, and all other spawn methods
 */
public class MobSpawnListener implements Listener {

    private final RealMobScale plugin;

    public MobSpawnListener() {
        this.plugin = RealMobScale.getInstance();
    }

    /**
     * Main handler for all creature spawns
     * Handles natural spawns, spawner spawns, eggs, commands, etc.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        // Log spawn reason for debugging
        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().fine("Creature spawn detected: " + entity.getType().name() +
                " (Reason: " + event.getSpawnReason().name() +
                ", World: " + entity.getWorld().getName() +
                ", Location: " + formatLocation(entity.getLocation()) + ")");
        }

        // Apply scaling with small delay to ensure entity is fully initialized
        // Use different delays based on spawn reason to optimize timing
        long delay = getOptimalDelay(event.getSpawnReason());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Double-check entity still exists and is valid
            if (entity.isValid() && !entity.isDead()) {
                boolean wasApplied = plugin.getMobScaleManager().applyRealisticScaling(entity);

                if (wasApplied) {
                    // Send debug messages to admins with debug mode enabled
                    double scaleFactor = entity.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null ?
                        entity.getAttribute(org.bukkit.attribute.Attribute.SCALE).getBaseValue() : 1.0;

                    plugin.getDebugManager().sendScalingDebugMessage(
                        "Applied scaling to spawned entity",
                        entity.getType().name(),
                        scaleFactor,
                        entity.getLocation()
                    );
                }
            }
        }, delay);
    }

    /**
     * Special handler for breeding events
     * Ensures baby animals from breeding are properly scaled
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityBreed(EntityBreedEvent event) {
        Entity child = event.getEntity();

        if (!(child instanceof LivingEntity)) {
            return;
        }

        LivingEntity baby = (LivingEntity) child;

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info("Baby entity born from breeding: " + baby.getType().name() +
                " (Parents: " + event.getFather().getType().name() +
                " + " + event.getMother().getType().name() +
                ", Location: " + formatLocation(baby.getLocation()) + ")");
        }

        // Apply scaling with slightly longer delay for breeding to ensure baby status is set
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (baby.isValid() && !baby.isDead()) {
                // Check if it's still a baby
                boolean isBaby = isBabyAnimal(baby);

                boolean wasApplied = plugin.getMobScaleManager().applyRealisticScaling(baby);

                if (wasApplied) {
                    // Send debug messages to admins with debug mode enabled
                    double scaleFactor = baby.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null ?
                        baby.getAttribute(org.bukkit.attribute.Attribute.SCALE).getBaseValue() : 1.0;

                    plugin.getDebugManager().sendScalingDebugMessage(
                        "Applied scaling to bred baby",
                        baby.getType().name() + (isBaby ? " (baby)" : ""),
                        scaleFactor,
                        baby.getLocation()
                    );
                }
            }
        }, 3L); // Longer delay for breeding to ensure baby status is properly set
    }

    /**
     * Fallback handler for any other entity spawns
     * Catches any spawns not handled by CreatureSpawnEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        // Only handle living entities
        if (!(entity instanceof LivingEntity)) {
            return;
        }

        LivingEntity livingEntity = (LivingEntity) entity;

        // Skip if this was already handled by CreatureSpawnEvent
        // This prevents duplicate scaling applications
        if (wasRecentlyHandled(livingEntity)) {
            return;
        }

        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().fine("Fallback entity spawn detected: " + livingEntity.getType().name() +
                " (World: " + livingEntity.getWorld().getName() +
                ", Location: " + formatLocation(livingEntity.getLocation()) + ")");
        }

        // Apply scaling with delay
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (livingEntity.isValid() && !livingEntity.isDead()) {
                boolean wasApplied = plugin.getMobScaleManager().applyRealisticScaling(livingEntity);

                if (wasApplied) {
                    // Send debug messages to admins with debug mode enabled
                    double scaleFactor = livingEntity.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null ?
                        livingEntity.getAttribute(org.bukkit.attribute.Attribute.SCALE).getBaseValue() : 1.0;

                    plugin.getDebugManager().sendScalingDebugMessage(
                        "Applied scaling via fallback",
                        livingEntity.getType().name(),
                        scaleFactor,
                        livingEntity.getLocation()
                    );
                }
            }
        }, 2L);
    }

    /**
     * Get optimal delay based on spawn reason
     */
    private long getOptimalDelay(CreatureSpawnEvent.SpawnReason reason) {
        switch (reason) {
            // Natural spawns need standard delay
            case NATURAL:
            case CHUNK_GEN:
                return 2L;

            // Spawner spawns may need longer delay
            case SPAWNER:
                return 3L;

            // Eggs and breeding need specific timing
            case EGG:
                return 2L;
            case BREEDING:
                return 3L; // Will be handled by dedicated breeding event

            // Commands and manual spawns
            case COMMAND:
            case DISPENSE_EGG:
                return 1L;

            // Default timing
            default:
                return 2L;
        }
    }

    /**
     * Check if entity is a baby animal
     */
    private boolean isBabyAnimal(LivingEntity entity) {
        // Check for ageable entities (most animals)
        if (entity instanceof org.bukkit.entity.Ageable) {
            return !((org.bukkit.entity.Ageable) entity).isAdult();
        }

        // Check for specific baby entity types
        String entityName = entity.getType().name().toLowerCase();
        return entityName.contains("baby") || entityName.contains("child");
    }

    /**
     * Format location for logging
     */
    private String formatLocation(org.bukkit.Location loc) {
        return String.format("(%.1f, %.1f, %.1f)", loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Simple check to avoid duplicate scaling applications
     * This is a basic implementation - could be enhanced with a proper tracking system
     */
    private boolean wasRecentlyHandled(LivingEntity entity) {
        // For now, we'll use a simple approach: check if the entity already has scaling applied
        // In a more advanced implementation, we could track recent scaling operations
        try {
            return entity.getAttribute(org.bukkit.attribute.Attribute.SCALE) != null &&
                   entity.getAttribute(org.bukkit.attribute.Attribute.SCALE).getBaseValue() != 1.0;
        } catch (Exception e) {
            return false;
        }
    }
}