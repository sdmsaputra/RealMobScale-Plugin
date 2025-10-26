package com.minekarta.realMobScale.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;

import com.minekarta.realMobScale.RealMobScale;

public class ConfigManager {
    private final RealMobScale plugin;
    private FileConfiguration config;
    private File configFile;
    
    public ConfigManager(RealMobScale plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        reloadConfig();
    }
    
    public void reloadConfig() {
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            plugin.saveResource("config.yml", false);
            config = YamlConfiguration.loadConfiguration(configFile);
        }
    }
    
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }
    
    public void saveConfig() {
        if (config == null || configFile == null) return;
        
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, e);
        }
    }
    
    public boolean isEnabled() {
        return getConfig().getBoolean("settings.enabled", true);
    }
    
    public boolean applyToExisting() {
        return getConfig().getBoolean("settings.apply-to-existing", false);
    }
    
    public boolean isDebug() {
        return getConfig().getBoolean("settings.debug", false);
    }

    public boolean isDebugMode() {
        return isDebug();
    }
    
    public String getWorldMode() {
        return getConfig().getString("worlds.mode", "whitelist");
    }
    
    public List<String> getWorldList() {
        List<String> worlds = getConfig().getStringList("worlds.list");
        if (worlds.isEmpty()) {
            worlds = new ArrayList<>();
            worlds.add("world");
            worlds.add("world_nether");
            worlds.add("world_the_end");
        }
        return worlds;
    }
    
    public boolean isWorldEnabled(String worldName) {
        List<String> worlds = getWorldList();
        boolean isInList = worlds.contains(worldName);
        
        // If mode is whitelist, the world must be in the list to be enabled
        // If mode is blacklist, the world must NOT be in the list to be enabled
        return "whitelist".equalsIgnoreCase(getWorldMode()) ? isInList : !isInList;
    }
    
    public boolean isMobEnabledByDefault() {
        return getConfig().getBoolean("mobs.enabled-by-default", true);
    }
    
    public boolean isMobEnabled(EntityType entityType) {
        // Check if there's an override for this specific mob
        String mobName = entityType.name();
        String overridePath = "mobs.overrides." + mobName + ".enabled";
        
        if (getConfig().contains(overridePath)) {
            return getConfig().getBoolean(overridePath, isMobEnabledByDefault());
        }
        
        // Return default if no override exists
        return isMobEnabledByDefault();
    }
    
    public double getCustomScale(EntityType entityType) {
        String mobName = entityType.name();
        String overridePath = "mobs.overrides." + mobName + ".custom-scale";
        
        if (getConfig().contains(overridePath)) {
            return getConfig().getDouble(overridePath);
        }
        
        // Return -1 if no custom scale is defined, indicating to use default
        return -1.0;
    }
    
    public double getCustomHealth(EntityType entityType) {
        String mobName = entityType.name();
        String overridePath = "mobs.overrides." + mobName + ".custom-health";

        if (getConfig().contains(overridePath)) {
            return getConfig().getDouble(overridePath);
        }

        // Return -1 if no custom health is defined, indicating to use default
        return -1.0;
    }

    public double getCustomBabyScale(EntityType entityType) {
        String mobName = entityType.name();
        String overridePath = "mobs.overrides." + mobName + ".custom-baby-scale";

        if (getConfig().contains(overridePath)) {
            return getConfig().getDouble(overridePath);
        }

        // Return -1 if no custom baby scale is defined, indicating to use default
        return -1.0;
    }
    
    public int getCheckInterval() {
        return getConfig().getInt("performance.check-interval", 100);
    }
    
    public int getMaxProcessingPerTick() {
        return getConfig().getInt("performance.max-processing-per-tick", 10);
    }
    
    public boolean respectOtherPlugins() {
        return getConfig().getBoolean("compatibility.respect-other-plugins", true);
    }
    
    public List<String> getCheckedPlugins() {
        return getConfig().getStringList("compatibility.checked-plugins");
    }

    // ==================== REALISTIC SETTINGS ====================

    public boolean isRealisticMode() {
        return getConfig().getBoolean("realistic.enabled", true);
    }

    public boolean enableBabyScaling() {
        return getConfig().getBoolean("realistic.baby-scaling", true);
    }

    public double getBabyScaleMultiplier() {
        return getConfig().getDouble("realistic.baby-scale-multiplier", 1.0);
    }

    public boolean enableRealisticHealth() {
        return getConfig().getBoolean("realistic.realistic-health", true);
    }

    public boolean enableRealisticSpeed() {
        return getConfig().getBoolean("realistic.realistic-speed", true);
    }

    public boolean enableRealisticDamage() {
        return getConfig().getBoolean("realistic.realistic-damage", true);
    }

    public boolean showDetailedInfo() {
        return getConfig().getBoolean("realistic.show-detailed-info", false);
    }

    public double getGlobalScaleMultiplier() {
        return getConfig().getDouble("realistic.global-scale-multiplier", 1.0);
    }

    public double getGlobalHealthMultiplier() {
        return getConfig().getDouble("realistic.global-health-multiplier", 1.0);
    }

    public boolean enableSizeCategories() {
        return getConfig().getBoolean("realistic.enable-size-categories", true);
    }

    public boolean respectWorldBorders() {
        return getConfig().getBoolean("realistic.respect-world-borders", true);
    }

    public String getLoggingLevel() {
        return getConfig().getString("realistic.logging-level", "INFO").toUpperCase();
    }

    public boolean enablePerformanceMode() {
        return getConfig().getBoolean("realistic.performance-mode", false);
    }

    public int getMaxMobsPerChunk() {
        return getConfig().getInt("realistic.max-mobs-per-chunk", 50);
    }

    // ==================== CATEGORY SETTINGS ====================

    public boolean areAnimalsEnabled() {
        return getConfig().getBoolean("categories.animals.enabled", true);
    }

    public boolean areWaterCreaturesEnabled() {
        return getConfig().getBoolean("categories.water-creatures.enabled", true);
    }

    public boolean areFlyingCreaturesEnabled() {
        return getConfig().getBoolean("categories.flying-creatures.enabled", true);
    }

    public boolean areArthropodsEnabled() {
        return getConfig().getBoolean("categories.arthropods.enabled", true);
    }

    // ==================== UTILITY METHODS ====================

    public boolean shouldScaleEntity(org.bukkit.entity.Entity entity) {
        EntityType type = entity.getType();

        // Check if basic scaling is enabled for this entity type
        if (!isMobEnabled(type)) {
            return false;
        }

        // Check category settings
        if (isAnimal(type) && !areAnimalsEnabled()) return false;
        if (isWaterCreature(type) && !areWaterCreaturesEnabled()) return false;
        if (isFlyingCreature(type) && !areFlyingCreaturesEnabled()) return false;
        if (isArthropod(type) && !areArthropodsEnabled()) return false;

        return true;
    }

    private boolean isAnimal(EntityType type) {
        return switch (type.name()) {
            case "COW", "PIG", "SHEEP", "CHICKEN", "RABBIT", "HORSE", "DONKEY", "MULE",
                 "LLAMA", "TRADER_LLAMA", "CAMEL", "CAT", "OCELOT", "WOLF", "FOX", "PARROT",
                 "TURTLE", "AXOLOTL", "FROG", "GOAT", "PANDA", "POLAR_BEAR" -> true;
            default -> false;
        };
    }

    private boolean isWaterCreature(EntityType type) {
        return switch (type.name()) {
            case "COD", "SALMON", "TROPICAL_FISH", "PUFFERFISH", "SQUID", "GLOW_SQUID",
                 "DOLPHIN", "TURTLE", "AXOLOTL" -> true;
            default -> false;
        };
    }

    private boolean isFlyingCreature(EntityType type) {
        return switch (type.name()) {
            case "BAT", "PARROT", "BEE" -> true;
            default -> false;
        };
    }

    private boolean isArthropod(EntityType type) {
        return switch (type.name()) {
            case "SPIDER", "CAVE_SPIDER", "SILVERFISH", "BEE" -> true;
            default -> false;
        };
    }

    /**
     * Check if a living entity should be scaled
     */
    public boolean shouldScaleEntity(org.bukkit.entity.LivingEntity entity) {
        // Check if the entity is in an enabled world
        if (!isWorldEnabled(entity.getWorld().getName())) {
            return false;
        }

        // Use the existing entity checking method
        return shouldScaleEntity((org.bukkit.entity.Entity) entity);
    }
}