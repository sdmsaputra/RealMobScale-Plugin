package com.minekarta.realMobScale.managers;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.minekarta.realMobScale.RealMobScale;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player-specific preferences for mob scaling
 * Allows players to customize their scaling experience
 */
public class PlayerPreferencesManager {
    private final RealMobScale plugin;
    private final Map<UUID, PlayerPreferences> playerPreferences;
    private File preferencesFile;
    private FileConfiguration preferencesConfig;

    public PlayerPreferencesManager(RealMobScale plugin) {
        this.plugin = plugin;
        this.playerPreferences = new ConcurrentHashMap<>();
        loadPreferences();
    }

    /**
     * Load player preferences from file
     */
    private void loadPreferences() {
        preferencesFile = new File(plugin.getDataFolder(), "player_preferences.yml");

        if (!preferencesFile.exists()) {
            try {
                preferencesFile.createNewFile();
                preferencesConfig = new YamlConfiguration();
                savePreferences();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create player preferences file: " + e.getMessage());
                return;
            }
        } else {
            preferencesConfig = YamlConfiguration.loadConfiguration(preferencesFile);
        }

        // Load existing preferences
        for (String key : preferencesConfig.getKeys(false)) {
            try {
                UUID playerUUID = UUID.fromString(key);
                PlayerPreferences prefs = new PlayerPreferences();
                prefs.setEnabled(preferencesConfig.getBoolean(key + ".enabled", true));
                prefs.setScalingMode(ScalingMode.valueOf(preferencesConfig.getString(key + ".scaling_mode", "GLOBAL")));
                prefs.setPersonalScaleMultiplier(preferencesConfig.getDouble(key + ".personal_scale_multiplier", 1.0));
                prefs.setShowScalingInfo(preferencesConfig.getBoolean(key + ".show_scaling_info", false));
                prefs.setCategoriesEnabled(preferencesConfig.getStringList(key + ".categories_enabled"));

                playerPreferences.put(playerUUID, prefs);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in preferences file: " + key);
            }
        }

        plugin.getLogger().info("Loaded preferences for " + playerPreferences.size() + " players");
    }

    /**
     * Save player preferences to file
     */
    public void savePreferences() {
        try {
            for (Map.Entry<UUID, PlayerPreferences> entry : playerPreferences.entrySet()) {
                String key = entry.getKey().toString();
                PlayerPreferences prefs = entry.getValue();

                preferencesConfig.set(key + ".enabled", prefs.isEnabled());
                preferencesConfig.set(key + ".scaling_mode", prefs.getScalingMode().name());
                preferencesConfig.set(key + ".personal_scale_multiplier", prefs.getPersonalScaleMultiplier());
                preferencesConfig.set(key + ".show_scaling_info", prefs.isShowScalingInfo());
                preferencesConfig.set(key + ".categories_enabled", prefs.getCategoriesEnabled());
            }

            preferencesConfig.save(preferencesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player preferences: " + e.getMessage());
        }
    }

    /**
     * Get preferences for a player (creates default if not exists)
     */
    public PlayerPreferences getPlayerPreferences(Player player) {
        return playerPreferences.computeIfAbsent(player.getUniqueId(), k -> new PlayerPreferences());
    }

    /**
     * Get preferences for a player by UUID
     */
    public PlayerPreferences getPlayerPreferences(UUID playerUUID) {
        return playerPreferences.get(playerUUID);
    }

    /**
     * Check if a player wants to see scaling
     */
    public boolean doesPlayerWantScaling(Player player) {
        PlayerPreferences prefs = getPlayerPreferences(player);
        return prefs.isEnabled();
    }

    /**
     * Get the effective scale multiplier for a player
     */
    public double getEffectiveScaleMultiplier(Player player, String entityType) {
        PlayerPreferences prefs = getPlayerPreferences(player);

        switch (prefs.getScalingMode()) {
            case GLOBAL:
                return prefs.getPersonalScaleMultiplier();
            case BY_CATEGORY:
                // Check if entity type is in enabled categories
                String category = getEntityCategory(entityType);
                if (prefs.getCategoriesEnabled().contains(category)) {
                    return prefs.getPersonalScaleMultiplier();
                }
                return 0.0; // No scaling for this category
            case DISABLED:
                return 0.0;
            default:
                return 1.0;
        }
    }

    /**
     * Get category for an entity type
     */
    private String getEntityCategory(String entityType) {
        entityType = entityType.toUpperCase();

        // Check categories similar to ConfigManager
        if (isAnimal(entityType)) return "ANIMALS";
        if (isMonster(entityType)) return "MONSTERS";
        if (isWaterCreature(entityType)) return "WATER_CREATURES";
        if (isFlyingCreature(entityType)) return "FLYING_CREATURES";
        if (isBoss(entityType)) return "BOSSES";

        return "OTHER";
    }

    private boolean isAnimal(String entityType) {
        return entityType.matches("COW|PIG|SHEEP|CHICKEN|MOOSHROOM|RABBIT|HORSE|DONKEY|MULE|LLAMA|TRADER_LLAMA|CAMEL|CAT|OCELOT|WOLF|FOX|PARROT|TURTLE|AXOLOTL|FROG|GOAT|PANDA|POLAR_BEAR");
    }

    private boolean isMonster(String entityType) {
        return entityType.matches("ZOMBIE|SKELETON|CREEPER|SPIDER|CAVE_SPIDER|ENDERMAN|WITCH|SILVERFISH|ENDERMITE|SLIME|MAGMA_CUBE|BLAZE|GHAST|PHANTOM|DROWNED|HUSK|STRAY|PILLAGER|VINDICATOR|EVOKER|ILLUSIONER|RAVAGER|WARDEN|PIGLIN|PIGLIN_BRUTE|HOGLIN|ZOGLIN|ZOMBIFIED_PIGLIN");
    }

    private boolean isWaterCreature(String entityType) {
        return entityType.matches("COD|SALMON|TROPICAL_FISH|PUFFERFISH|SQUID|GLOW_SQUID|DOLPHIN|TURTLE|GUARDIAN|ELDER_GUARDIAN|AXOLOTL");
    }

    private boolean isFlyingCreature(String entityType) {
        return entityType.matches("BAT|PARROT|BEE|PHANTOM|GHAST|BLAZE|ENDER_DRAGON|WITHER");
    }

    private boolean isBoss(String entityType) {
        return entityType.matches("ENDER_DRAGON|WITHER|ELDER_GUARDIAN|WARDEN");
    }

    /**
     * Remove preferences for a player (called when player leaves)
     */
    public void removePlayerPreferences(UUID playerUUID) {
        // Don't remove from map immediately, just save to file
        // Preferences should persist between sessions
    }

    /**
     * Clear all preferences (for admin use)
     */
    public void clearAllPreferences() {
        playerPreferences.clear();
        preferencesConfig.getKeys(false).forEach(key -> preferencesConfig.set(key, null));
        savePreferences();
    }

    /**
     * Get the number of players with preferences
     */
    public int getPreferencesCount() {
        return playerPreferences.size();
    }

    /**
     * Inner class to represent player preferences
     */
    public static class PlayerPreferences {
        private boolean enabled = true;
        private ScalingMode scalingMode = ScalingMode.GLOBAL;
        private double personalScaleMultiplier = 1.0;
        private boolean showScalingInfo = false;
        private java.util.List<String> categoriesEnabled = java.util.Arrays.asList(
            "ANIMALS", "MONSTERS", "WATER_CREATURES", "FLYING_CREATURES", "BOSSES"
        );

        // Getters and Setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public ScalingMode getScalingMode() { return scalingMode; }
        public void setScalingMode(ScalingMode scalingMode) { this.scalingMode = scalingMode; }

        public double getPersonalScaleMultiplier() { return personalScaleMultiplier; }
        public void setPersonalScaleMultiplier(double multiplier) { this.personalScaleMultiplier = multiplier; }

        public boolean isShowScalingInfo() { return showScalingInfo; }
        public void setShowScalingInfo(boolean show) { this.showScalingInfo = show; }

        public java.util.List<String> getCategoriesEnabled() { return categoriesEnabled; }
        public void setCategoriesEnabled(java.util.List<String> categories) { this.categoriesEnabled = categories; }
    }

    /**
     * Scaling modes for players
     */
    public enum ScalingMode {
        GLOBAL,        // All scaling with personal multiplier
        BY_CATEGORY,   // Only specific categories
        DISABLED       // No scaling for this player
    }
}