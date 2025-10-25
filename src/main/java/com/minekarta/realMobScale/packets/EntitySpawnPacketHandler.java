package com.minekarta.realMobScale.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import com.minekarta.realMobScale.RealMobScale;
import com.minekarta.realMobScale.data.MobData;
import com.minekarta.realMobScale.data.ScaleProfile;

/**
 * Handles entity spawn packets to ensure proper scaling is applied immediately on spawn
 * This ensures that clients receive the correct scale information when entities first appear
 */
public class EntitySpawnPacketHandler extends PacketListenerAbstract {

    private final RealMobScale plugin;
    private final EntityMetadataPacketHandler metadataHandler;

    public EntitySpawnPacketHandler(RealMobScale plugin, EntityMetadataPacketHandler metadataHandler) {
        this.plugin = plugin;
        this.metadataHandler = metadataHandler;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        // Handle living entity spawns
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_LIVING_ENTITY) {
            handleLivingEntitySpawn(event);
        }
        // Handle regular entity spawns (for non-living entities that might need scaling)
        else if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
            handleEntitySpawn(event);
        }
    }

    private void handleLivingEntitySpawn(PacketSendEvent event) {
        try {
            WrapperPlayServerSpawnLivingEntity packet = new WrapperPlayServerSpawnLivingEntity(event);
            int entityId = packet.getEntityId();
            EntityType packetEntityType = packet.getEntityType();

            // Convert PacketEvents entity type to Bukkit entity type
            org.bukkit.entity.EntityType bukkitType = convertPacketEventsToBukkitType(packetEntityType);
            if (bukkitType == null) {
                return;
            }

            // Get the entity from the server to check if it should be scaled
            LivingEntity livingEntity = null;
            for (org.bukkit.World world : plugin.getServer().getWorlds()) {
                for (org.bukkit.entity.Entity entity : world.getEntities()) {
                    if (entity.getEntityId() == entityId && entity instanceof LivingEntity) {
                        livingEntity = (LivingEntity) entity;
                        break;
                    }
                }
                if (livingEntity != null) break;
            }

            if (livingEntity == null) {
                return;
            }

            // Check if this entity should be scaled
            if (!plugin.getConfigManager().shouldScaleEntity(livingEntity)) {
                return;
            }

            // Get scale profile for this entity
            ScaleProfile profile = MobData.getScaleProfile(livingEntity.getType());
            if (profile == null) {
                return;
            }

            // Calculate the scale factor with biome scaling
            float baseScaleFactor = calculateScaleFactor(livingEntity, profile);
            float biomeMultiplier = (float) plugin.getBiomeScalingManager().getBiomeScaleMultiplier(
                livingEntity.getLocation().getBlock().getBiome(),
                livingEntity.getType().name()
            );
            float scaleFactor = (float) (baseScaleFactor * biomeMultiplier);

            // Store the scale information in the metadata handler
            metadataHandler.addEntityScale(livingEntity.getUniqueId(), livingEntity.getEntityId(), profile, scaleFactor);

            // The actual scale data will be sent in the metadata packet that follows
            // This handler ensures we track the entity from the moment it spawns

            if (plugin.getConfigManager().isDebugMode()) {
                String entityTypeStr = livingEntity.getType().name();
                String ageInfo = isBabyAnimal(livingEntity) ? "(Baby)" : "(Adult)";
                // Spawn packet tracked for " + entityTypeStr + " " + ageInfo +
                // " (ID: " + entityId + ", Scale: " + scaleFactor + "x)
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Error handling living entity spawn packet: " + e.getMessage());
            if (plugin.getConfigManager().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    private void handleEntitySpawn(PacketSendEvent event) {
        try {
            WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(event);
            int entityId = packet.getEntityId();
            EntityType packetEntityType = packet.getEntityType();

            // Some non-living entities might still need scaling (like armor stands with custom models)
            // For now, we focus on living entities, but this structure allows future expansion

            if (plugin.getConfigManager().isDebugMode()) {
                plugin.getLogger().fine("Entity spawn packet processed: " + packetEntityType + " (ID: " + entityId + ")");
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Error handling entity spawn packet: " + e.getMessage());
            if (plugin.getConfigManager().isDebugMode()) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Convert PacketEvents EntityType to Bukkit EntityType
     */
    private org.bukkit.entity.EntityType convertPacketEventsToBukkitType(EntityType packetType) {
        try {
            // Try to convert using toString() since name() method might not exist
            String packetName = packetType.toString().toUpperCase();

            // Map common entity types that might have different names
            switch (packetName) {
                case "FALLING_BLOCK":
                    return org.bukkit.entity.EntityType.FALLING_BLOCK;
                case "EXPERIENCE_ORB":
                    return org.bukkit.entity.EntityType.EXPERIENCE_ORB;
                case "LEASH_KNOT":
                    return org.bukkit.entity.EntityType.LEASH_KNOT; // Use LEASH_KNOT instead of LEASH_HITCH
                case "PAINTING":
                    return org.bukkit.entity.EntityType.PAINTING;
                case "ITEM_FRAME":
                    return org.bukkit.entity.EntityType.ITEM_FRAME;
                case "ARMOR_STAND":
                    return org.bukkit.entity.EntityType.ARMOR_STAND;
                default:
                    // Try direct name matching
                    try {
                        return org.bukkit.entity.EntityType.valueOf(packetName);
                    } catch (IllegalArgumentException ex) {
                        // Try case-insensitive matching
                        for (org.bukkit.entity.EntityType bukkitType : org.bukkit.entity.EntityType.values()) {
                            if (bukkitType.name().equalsIgnoreCase(packetName)) {
                                return bukkitType;
                            }
                        }
                    }

                    plugin.getLogger().warning("Could not map entity type: " + packetName);
                    return null;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error converting entity type: " + e.getMessage());
            return null;
        }
    }

    /**
     * Calculate the scale factor for an entity
     */
    private float calculateScaleFactor(LivingEntity entity, ScaleProfile profile) {
        float baseScale = (float) profile.getScaleFactor();

        // Apply global scale multiplier
        float globalMultiplier = (float) plugin.getConfigManager().getGlobalScaleMultiplier();
        baseScale *= globalMultiplier;

        // Apply baby scaling if applicable
        if (isBabyAnimal(entity)) {
            // Check for custom baby scale first
            double customBabyScale = plugin.getConfigManager().getCustomBabyScale(entity.getType());
            if (customBabyScale > 0) {
                baseScale *= customBabyScale * plugin.getConfigManager().getBabyScaleMultiplier();
            } else {
                baseScale *= profile.getBabyScaleFactor() * plugin.getConfigManager().getBabyScaleMultiplier();
            }
        }

        return baseScale;
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
        return entityName.contains("baby") || entityName.contains("child");
    }
}