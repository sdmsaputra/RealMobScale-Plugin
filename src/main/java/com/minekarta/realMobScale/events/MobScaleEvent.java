package com.minekarta.realMobScale.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.minekarta.realMobScale.data.ScaleProfile;

/**
 * Event that is fired when a mob is about to be scaled.
 * This event allows plugins to modify or cancel the scaling operation.
 */
public class MobScaleEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final LivingEntity entity;
    private final ScaleProfile originalProfile;
    private ScaleProfile modifiedProfile;
    private double scaleFactor;
    private boolean cancelled;

    public MobScaleEvent(LivingEntity entity, ScaleProfile profile, double scaleFactor) {
        this.entity = entity;
        this.originalProfile = profile;
        this.modifiedProfile = profile;
        this.scaleFactor = scaleFactor;
        this.cancelled = false;
    }

    /**
     * Get the entity that is being scaled
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Get the original scale profile (before any modifications)
     */
    public ScaleProfile getOriginalProfile() {
        return originalProfile;
    }

    /**
     * Get the current scale profile (may have been modified)
     */
    public ScaleProfile getProfile() {
        return modifiedProfile;
    }

    /**
     * Set a new scale profile for this entity
     */
    public void setProfile(ScaleProfile profile) {
        this.modifiedProfile = profile;
    }

    /**
     * Get the scale factor that will be applied
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Set a new scale factor for this entity
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}