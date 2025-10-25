package com.minekarta.realMobScale.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.Map;

/**
 * Event that is fired when the RealMobScale configuration is reloaded.
 * This allows other plugins to react to configuration changes.
 */
public class MobScaleConfigReloadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String reloadSource;
    private final long reloadTime;
    private final Map<String, Object> changedSettings;

    public MobScaleConfigReloadEvent(String reloadSource, Map<String, Object> changedSettings) {
        this.reloadSource = reloadSource;
        this.reloadTime = System.currentTimeMillis();
        this.changedSettings = changedSettings;
    }

    /**
     * Get the source of the reload (e.g., "command", "file_change")
     */
    public String getReloadSource() {
        return reloadSource;
    }

    /**
     * Get the time when configuration was reloaded (timestamp)
     */
    public long getReloadTime() {
        return reloadTime;
    }

    /**
     * Get a map of changed settings
     */
    public Map<String, Object> getChangedSettings() {
        return changedSettings;
    }

    /**
     * Check if a specific setting was changed
     */
    public boolean wasSettingChanged(String settingPath) {
        return changedSettings.containsKey(settingPath);
    }

    /**
     * Get the new value of a changed setting
     */
    public Object getNewValue(String settingPath) {
        return changedSettings.get(settingPath);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}