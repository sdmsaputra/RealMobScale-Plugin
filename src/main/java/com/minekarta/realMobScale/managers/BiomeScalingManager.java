package com.minekarta.realMobScale.managers;

import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.minekarta.realMobScale.RealMobScale;
import com.minekarta.realMobScale.data.ScaleProfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages biome-specific scaling adjustments
 * Allows different scale multipliers based on the biome where entities spawn
 */
public class BiomeScalingManager {
    private final RealMobScale plugin;
    private final ConfigManager configManager;
    private Map<String, BiomeScalingData> biomeMultipliers;
    private File biomeConfigFile;
    private FileConfiguration biomeConfig;

    public BiomeScalingManager(RealMobScale plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        loadBiomeConfig();
    }

    /**
     * Load biome configuration from file
     */
    private void loadBiomeConfig() {
        biomeConfigFile = new File(plugin.getDataFolder(), "biome_scaling.yml");

        if (!biomeConfigFile.exists()) {
            createDefaultBiomeConfig();
        } else {
            biomeConfig = YamlConfiguration.loadConfiguration(biomeConfigFile);
        }

        loadBiomeMultipliers();
    }

    /**
     * Create default biome configuration
     */
    private void createDefaultBiomeConfig() {
        try {
            biomeConfigFile.createNewFile();
            biomeConfig = new YamlConfiguration();

            // Default biome multipliers based on real-world adaptations
            setDefaultBiomeMultipliers();

            biomeConfig.save(biomeConfigFile);
            plugin.getLogger().info("Created default biome scaling configuration");
        } catch (IOException e) {
            plugin.getLogger().warning("Could not create biome config file: " + e.getMessage());
        }
    }

    /**
     * Set default biome multipliers based on real-world animal adaptations
     */
    private void setDefaultBiomeMultipliers() {
        // Cold biomes - animals might be larger for warmth
        biomeConfig.set("biomes.SNOWY_PLAINS.multiplier", 1.1);
        biomeConfig.set("biomes.ICE_SPIKES.multiplier", 1.1);
        biomeConfig.set("biomes.SNOWY_TAIGA.multiplier", 1.05);
        biomeConfig.set("biomes.FROZEN_PEAKS.multiplier", 1.0);
        biomeConfig.set("biomes.FROZEN_OCEAN.multiplier", 0.95);

        // Hot biomes - animals might be smaller for heat dissipation
        biomeConfig.set("biomes.DESERT.multiplier", 0.9);
        biomeConfig.set("biomes.BADLANDS.multiplier", 0.95);
        biomeConfig.set("biomes.SAVANNA.multiplier", 0.95);
        biomeConfig.set("biomes.DESERT_OCEAN.multiplier", 0.9);

        // Forest biomes - standard size with slight variations
        biomeConfig.set("biomes.FOREST.multiplier", 1.0);
        biomeConfig.set("biomes.BIRCH_FOREST.multiplier", 1.0);
        biomeConfig.set("biomes.DARK_FOREST.multiplier", 1.05);
        biomeConfig.set("biomes.OLD_GROWTH_FOREST.multiplier", 1.02);
        biomeConfig.set("biomes.TAIGA.multiplier", 1.0);
        biomeConfig.set("biomes.GROVE.multiplier", 1.0);

        // Jungle biomes - larger due to abundant resources
        biomeConfig.set("biomes.JUNGLE.multiplier", 1.05);
        biomeConfig.set("biomes.SPARSE_JUNGLE.multiplier", 1.02);
        biomeConfig.set("biomes.BAMBOO_JUNGLE.multiplier", 1.05);

        // Mountain biomes - smaller due to limited resources and altitude
        biomeConfig.set("biomes.MOUNTAINS.multiplier", 0.95);
        biomeConfig.set("biomes.WINDSWEPT_HILLS.multiplier", 0.95);
        biomeConfig.set("biomes.STONY_PEAKS.multiplier", 0.9);
        biomeConfig.set("biomes.JAGGED_PEAKS.multiplier", 0.85);

        // Ocean biomes - varied based on depth and resources
        biomeConfig.set("biomes.OCEAN.multiplier", 1.0);
        biomeConfig.set("biomes.DEEP_OCEAN.multiplier", 1.1);
        biomeConfig.set("biomes.COLD_OCEAN.multiplier", 1.05);
        biomeConfig.set("biomes.LUKEWARM_OCEAN.multiplier", 1.0);
        biomeConfig.set("biomes.WARM_OCEAN.multiplier", 0.95);
        biomeConfig.set("biomes.DEEP_LUKEWARM_OCEAN.multiplier", 1.1);
        biomeConfig.set("biomes.DEEP_COLD_OCEAN.multiplier", 1.15);

        // Swamp biomes - larger due to abundant water resources
        biomeConfig.set("biomes.SWAMP.multiplier", 1.05);
        biomeConfig.set("biomes.MANGROVE_SWAMP.multiplier", 1.05);

        // Plains biomes - standard size
        biomeConfig.set("biomes.PLAINS.multiplier", 1.0);
        biomeConfig.set("biomes.SUNFLOWER_PLAINS.multiplier", 1.0);
        biomeConfig.set("biomes.MEADOW.multiplier", 1.0);

        // River biomes - slightly larger due to abundant resources
        biomeConfig.set("biomes.RIVER.multiplier", 1.02);
        biomeConfig.set("biomes.FROZEN_RIVER.multiplier", 1.02);

        // Mushroom biomes - unique size variations
        biomeConfig.set("biomes.MUSHROOM_FIELDS.multiplier", 0.9);

        // Nether biomes - harsh environment effects
        biomeConfig.set("biomes.NETHER_WASTES.multiplier", 0.95);
        biomeConfig.set("biomes.CRIMSON_FOREST.multiplier", 0.9);
        biomeConfig.set("biomes.WARPED_FOREST.multiplier", 0.9);
        biomeConfig.set("biomes.SOUL_SAND_VALLEY.multiplier", 0.85);
        biomeConfig.set("biomes.BASALT_DELTAS.multiplier", 0.8);

        // End biomes - alien environment effects
        biomeConfig.set("biomes.THE_END.multiplier", 0.9);
        biomeConfig.set("biomes.END_HIGHLANDS.multiplier", 0.85);
        biomeConfig.set("biomes.END_MIDLANDS.multiplier", 0.9);

        // Global settings
        biomeConfig.set("settings.enabled", true);
        biomeConfig.set("settings.minimum_multiplier", 0.5);
        biomeConfig.set("settings.maximum_multiplier", 2.0);
        biomeConfig.set("settings.apply_to_all_entities", false);
        biomeConfig.set("settings.debug_mode", false);
    }

    /**
     * Load biome multipliers from configuration
     */
    private void loadBiomeMultipliers() {
        biomeMultipliers = new HashMap<>();

        if (!biomeConfig.getBoolean("settings.enabled", true)) {
            plugin.getLogger().info("Biome-specific scaling is disabled in configuration");
            return;
        }

        double minMultiplier = biomeConfig.getDouble("settings.minimum_multiplier", 0.5);
        double maxMultiplier = biomeConfig.getDouble("settings.maximum_multiplier", 2.0);

        if (biomeConfig.contains("biomes")) {
            for (String biomeName : biomeConfig.getConfigurationSection("biomes").getKeys(false)) {
                double multiplier = biomeConfig.getDouble("biomes." + biomeName + ".multiplier", 1.0);

                // Clamp to min/max values
                multiplier = Math.max(minMultiplier, Math.min(maxMultiplier, multiplier));

                biomeMultipliers.put(biomeName, new BiomeScalingData(multiplier));
            }
        }

        plugin.getLogger().info("Loaded biome scaling data for " + biomeMultipliers.size() + " biomes");
    }

    /**
     * Get the scale multiplier for a specific biome and entity type
     */
    public double getBiomeScaleMultiplier(Biome biome, String entityType) {
        // Check if biome scaling is enabled globally
        if (!isBiomeScalingEnabled()) {
            return 1.0;
        }

        // Check if biome scaling applies to all entities or specific ones
        boolean applyToAll = biomeConfig.getBoolean("settings.apply_to_all_entities", false);
        if (!applyToAll && !shouldApplyToEntity(entityType)) {
            return 1.0;
        }

        String biomeKey = biome.getKey().getKey().toUpperCase();

        // Direct biome match
        if (biomeMultipliers.containsKey(biomeKey)) {
            double multiplier = biomeMultipliers.get(biomeKey).getMultiplier();
            if (isDebugMode()) {
                plugin.getLogger().fine("Applied biome multiplier " + multiplier + " for biome " + biomeKey + " and entity " + entityType);
            }
            return multiplier;
        }

        // Try to match biome family (e.g., OCEAN family)
        for (Map.Entry<String, BiomeScalingData> entry : biomeMultipliers.entrySet()) {
            String configBiome = entry.getKey();
            if (isBiomeFamilyMatch(biomeKey, configBiome)) {
                double multiplier = entry.getValue().getMultiplier();
                if (isDebugMode()) {
                    plugin.getLogger().fine("Applied family biome multiplier " + multiplier + " for biome " + biomeKey + " (matched " + configBiome + ")");
                }
                return multiplier;
            }
        }

        return 1.0; // Default multiplier
    }

    /**
     * Check if biome scaling is enabled
     */
    public boolean isBiomeScalingEnabled() {
        return biomeConfig != null && biomeConfig.getBoolean("settings.enabled", true);
    }

    /**
     * Check if debug mode is enabled
     */
    public boolean isDebugMode() {
        return biomeConfig != null && biomeConfig.getBoolean("settings.debug_mode", false);
    }

    /**
     * Check if biome scaling should apply to a specific entity type
     */
    private boolean shouldApplyToEntity(String entityType) {
        // Default to animals and monsters, but not bosses or special entities
        String entityUpper = entityType.toUpperCase();

        // Include common animals and monsters
        if (entityUpper.matches("COW|PIG|SHEEP|CHICKEN|RABBIT|WOLF|FOX|CAT|HORSE")) {
            return true;
        }

        if (entityUpper.matches("ZOMBIE|SKELETON|CREEPER|SPIDER|ENDERMAN")) {
            return true;
        }

        // Exclude bosses and special entities by default
        if (entityUpper.matches("ENDER_DRAGON|WITHER|WARDEN|ELDER_GUARDIAN")) {
            return false;
        }

        return true; // Default to including other entities
    }

    /**
     * Check if two biomes are in the same family
     */
    private boolean isBiomeFamilyMatch(String biome1, String biome2) {
        // Ocean family
        if ((biome1.contains("OCEAN") || biome2.contains("OCEAN")) &&
            (biome1.startsWith("OCEAN") || biome2.startsWith("OCEAN") ||
             biome1.startsWith("DEEP") || biome2.startsWith("DEEP") ||
             biome1.startsWith("COLD") || biome2.startsWith("COLD") ||
             biome1.startsWith("WARM") || biome2.startsWith("WARM") ||
             biome1.startsWith("LUKEWARM") || biome2.startsWith("LUKEWARM"))) {
            return true;
        }

        // Forest family
        if ((biome1.contains("FOREST") || biome2.contains("FOREST")) ||
            (biome1.contains("TAIGA") || biome2.contains("TAIGA")) ||
            (biome1.contains("BIRCH") || biome2.contains("BIRCH")) ||
            (biome1.contains("GROVE") || biome2.contains("GROVE"))) {
            return true;
        }

        // Mountain family
        if ((biome1.contains("MOUNTAIN") || biome2.contains("MOUNTAIN")) ||
            (biome1.contains("PEAK") || biome2.contains("PEAK")) ||
            (biome1.contains("HILLS") || biome2.contains("HILLS")) ||
            (biome1.contains("WINDSWEPT") || biome2.contains("WINDSWEPT"))) {
            return true;
        }

        // Desert family
        if ((biome1.contains("DESERT") || biome2.contains("DESERT")) ||
            (biome1.contains("BADLANDS") || biome2.contains("BADLANDS"))) {
            return true;
        }

        return false;
    }

    /**
     * Reload biome configuration
     */
    public void reloadBiomeConfig() {
        if (biomeConfigFile != null && biomeConfigFile.exists()) {
            biomeConfig = YamlConfiguration.loadConfiguration(biomeConfigFile);
            loadBiomeMultipliers();
            plugin.getLogger().info("Biome scaling configuration reloaded");
        }
    }

    /**
     * Get the number of configured biomes
     */
    public int getConfiguredBiomeCount() {
        return biomeMultipliers != null ? biomeMultipliers.size() : 0;
    }

    /**
     * Inner class to store biome scaling data
     */
    private static class BiomeScalingData {
        private final double multiplier;

        public BiomeScalingData(double multiplier) {
            this.multiplier = multiplier;
        }

        public double getMultiplier() {
            return multiplier;
        }
    }
}