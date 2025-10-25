package com.minekarta.realMobScale.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import org.bukkit.entity.LivingEntity;
import com.minekarta.realMobScale.RealMobScale;
import com.minekarta.realMobScale.data.MobData;
import com.minekarta.realMobScale.data.ScaleProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles entity metadata packets to ensure smooth visual scaling across all clients
 * This class intercepts and modifies entity metadata packets to include scaling information
 */
public class EntityMetadataPacketHandler extends PacketListenerAbstract {

    private final RealMobScale plugin;
    private final ConcurrentHashMap<UUID, ScaleProfile> scaledEntities;
    private final ConcurrentHashMap<UUID, Float> entityScales;

    public EntityMetadataPacketHandler(RealMobScale plugin) {
        this.plugin = plugin;
        this.scaledEntities = new ConcurrentHashMap<>();
        this.entityScales = new ConcurrentHashMap<>();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) {
            return;
        }

        try {
            WrapperPlayServerEntityMetadata packet = new WrapperPlayServerEntityMetadata(event);
            int entityId = packet.getEntityId();

            // Find the entity in the server world
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

            // Check if this entity should be scaled globally
            if (!plugin.getConfigManager().shouldScaleEntity(livingEntity)) {
                return;
            }

            // Get scale profile for this entity
            ScaleProfile profile = MobData.getScaleProfile(livingEntity.getType());
            if (profile == null) {
                return;
            }

            // Calculate the scale factor
            float baseScaleFactor = calculateScaleFactor(livingEntity, profile);
            float biomeMultiplier = (float) plugin.getBiomeScalingManager().getBiomeScaleMultiplier(
                livingEntity.getLocation().getBlock().getBiome(),
                livingEntity.getType().name()
            );
            float scaleFactor = (float) (baseScaleFactor * biomeMultiplier);

            // Store the scale information
            scaledEntities.put(livingEntity.getUniqueId(), profile);
            entityScales.put(livingEntity.getUniqueId(), scaleFactor);

            // Modify the metadata to include scaling information
            List<com.github.retrooper.packetevents.protocol.entity.data.EntityData<?>> metadata =
                new ArrayList<>(packet.getEntityMetadata());

            // Try to remove existing scale data (index 17 is typically scale in newer versions)
            metadata.removeIf(data -> data.getIndex() == 17);

            // Add scale metadata using index 17 (common for scale)
            com.github.retrooper.packetevents.protocol.entity.data.EntityData<?> scaleData =
                new com.github.retrooper.packetevents.protocol.entity.data.EntityData<>(17,
                com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes.FLOAT, scaleFactor);
            metadata.add(scaleData);

            // Update the packet with modified metadata
            packet.setEntityMetadata(metadata);

            if (plugin.getConfigManager().isDebugMode()) {
                String entityTypeStr = livingEntity.getType().name();
                String ageInfo = isBabyAnimal(livingEntity) ? "(Baby)" : "(Adult)";
                plugin.getLogger().info("Packet scaling applied to " + entityTypeStr + " " + ageInfo +
                    " (ID: " + entityId + ", Scale: " + scaleFactor + "x)");
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Error handling entity metadata packet: " + e.getMessage());
            if (plugin.getConfigManager().isDebugMode()) {
                e.printStackTrace();
            }
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

    /**
     * Get the scale profile for a specific entity
     */
    public ScaleProfile getScaleProfile(UUID entityUUID) {
        return scaledEntities.get(entityUUID);
    }

    /**
     * Get the scale factor for a specific entity
     */
    public float getScaleFactor(UUID entityUUID) {
        return entityScales.getOrDefault(entityUUID, 1.0f);
    }

    /**
     * Remove an entity from the tracking system (called when entity is removed)
     */
    public void removeEntity(UUID entityUUID) {
        scaledEntities.remove(entityUUID);
        entityScales.remove(entityUUID);
    }

    /**
     * Check if an entity is being tracked for scaling
     */
    public boolean isEntityScaled(UUID entityUUID) {
        return scaledEntities.containsKey(entityUUID);
    }

    /**
     * Get the number of currently tracked entities
     */
    public int getTrackedEntityCount() {
        return scaledEntities.size();
    }

    /**
     * Clear all tracked entities (called on plugin disable)
     */
    public void clearAllEntities() {
        scaledEntities.clear();
        entityScales.clear();
    }

    /**
     * Add entity scale information (called by spawn handler)
     */
    public void addEntityScale(UUID entityUUID, ScaleProfile profile, float scaleFactor) {
        scaledEntities.put(entityUUID, profile);
        entityScales.put(entityUUID, scaleFactor);
    }
}