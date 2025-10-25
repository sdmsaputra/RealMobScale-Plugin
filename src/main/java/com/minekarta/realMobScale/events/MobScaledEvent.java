package com.minekarta.realMobScale.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.minekarta.realMobScale.data.ScaleProfile;

/**
 * Event that is fired after a mob has been successfully scaled.
 * This is a read-only event for informational purposes.
 */
public class MobScaledEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final LivingEntity entity;
    private final ScaleProfile appliedProfile;
    private final double appliedScaleFactor;
    private final long scalingTime;

    public MobScaledEvent(LivingEntity entity, ScaleProfile profile, double scaleFactor) {
        this.entity = entity;
        this.appliedProfile = profile;
        this.appliedScaleFactor = scaleFactor;
        this.scalingTime = System.currentTimeMillis();
    }

    /**
     * Get the entity that was scaled
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Get the scale profile that was applied
     */
    public ScaleProfile getAppliedProfile() {
        return appliedProfile;
    }

    /**
     * Get the scale factor that was applied
     */
    public double getAppliedScaleFactor() {
        return appliedScaleFactor;
    }

    /**
     * Get the time when scaling was applied (timestamp)
     */
    public long getScalingTime() {
        return scalingTime;
    }

    /**
     * Check if this entity is a baby
     */
    public boolean isBaby() {
        if (entity instanceof org.bukkit.entity.Ageable) {
            return !((org.bukkit.entity.Ageable) entity).isAdult();
        }
        String entityName = entity.getType().name().toLowerCase();
        return entityName.contains("baby") || entityName.contains("child");
    }

    /**
     * Get the entity type name
     */
    public String getEntityType() {
        return entity.getType().name();
    }

    /**
     * Get the real-world height from the applied profile
     */
    public double getRealWorldHeight() {
        return appliedProfile.getRealWorldHeight();
    }

    /**
     * Get the description from the applied profile
     */
    public String getDescription() {
        return appliedProfile.getDescription();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}