package com.minekarta.realMobScale.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for tab completion suggestions
 * Provides commonly used suggestions for commands
 */
public class TabCompletionUtils {

    // Common numeric values for tab completion
    public static final List<String> COMMON_MULTIPLIERS = Arrays.asList(
        "0.1", "0.25", "0.5", "0.75", "1.0", "1.25", "1.5", "2.0", "2.5", "3.0", "5.0", "10.0"
    );

    public static final List<String> COMMON_RADII = Arrays.asList(
        "5", "10", "25", "50", "100", "150", "200", "300", "500", "1000"
    );

    public static final List<String> COMMON_TICKS = Arrays.asList(
        "1", "5", "10", "20", "50", "100", "200"
    );

    // Entity types grouped by category
    public static final Map<String, List<String>> ENTITY_CATEGORIES = new HashMap<>();

    static {
        // Farm animals
        ENTITY_CATEGORIES.put("farm_animals", Arrays.asList(
            "COW", "PIG", "SHEEP", "CHICKEN", "MOOSHROOM", "RABBIT",
            "HORSE", "DONKEY", "MULE", "LLAMA", "TRADER_LLAMA", "CAMEL",
            "CAT", "OCELOT", "WOLF", "FOX", "PARROT", "TURTLE", "AXOLOTL",
            "FROG", "GOAT", "PANDA", "POLAR_BEAR", "TURKEY"
        ));

        // Hostile mobs
        ENTITY_CATEGORIES.put("hostile_mobs", Arrays.asList(
            "ZOMBIE", "SKELETON", "CREEPER", "SPIDER", "CAVE_SPIDER", "ENDERMAN",
            "WITCH", "SILVERFISH", "ENDERMITE", "SLIME", "MAGMA_CUBE", "BLAZE",
            "GHAST", "PHANTOM", "DROWNED", "HUSK", "STRAY", "PILLAGER",
            "VINDICATOR", "EVOKER", "ILLUSIONER", "RAVAGER", "WARDEN", "PIGLIN",
            "PIGLIN_BRUTE", "HOGLIN", "ZOGLIN", "ZOMBIFIED_PIGLIN"
        ));

        // Water creatures
        ENTITY_CATEGORIES.put("water_creatures", Arrays.asList(
            "COD", "SALMON", "TROPICAL_FISH", "PUFFERFISH", "SQUID", "GLOW_SQUID",
            "DOLPHIN", "TURTLE", "GUARDIAN", "ELDER_GUARDIAN", "AXOLOTL"
        ));

        // Flying creatures
        ENTITY_CATEGORIES.put("flying_creatures", Arrays.asList(
            "BAT", "PARROT", "BEE", "PHANTOM", "GHAST", "BLAZE", "ENDER_DRAGON", "WITHER"
        ));

        // Boss mobs
        ENTITY_CATEGORIES.put("boss_mobs", Arrays.asList(
            "ENDER_DRAGON", "WITHER", "ELDER_GUARDIAN", "WARDEN"
        ));

        // Passive mobs
        ENTITY_CATEGORIES.put("passive_mobs", Arrays.asList(
            "VILLAGER", "WANDERING_TRADER", "IRON_GOLEM", "SNOW_GOLEM"
        ));

        // Utility mobs
        ENTITY_CATEGORIES.put("utility_mobs", Arrays.asList(
            "ITEM_FRAME", "PAINTING", "ARMOR_STAND", "MINECART", "BOAT", "END_CRYSTAL"
        ));
    }

    /**
     * Filter a list of strings to only include those starting with the given prefix
     */
    public static List<String> filterStartingWith(List<String> list, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>(list);
        }

        String lowerPrefix = prefix.toLowerCase();
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerPrefix))
                .collect(Collectors.toList());
    }

    /**
     * Get all entity types that match the given prefix
     */
    public static List<String> getEntityTypes(String prefix) {
        return Arrays.stream(EntityType.values())
                .map(Enum::name)
                .filter(name -> prefix == null || prefix.isEmpty() || name.toLowerCase().startsWith(prefix.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get entity types from a specific category
     */
    public static List<String> getEntityTypesFromCategory(String category, String prefix) {
        List<String> categoryEntities = ENTITY_CATEGORIES.get(category.toLowerCase());
        if (categoryEntities == null) {
            return Collections.emptyList();
        }

        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>(categoryEntities);
        }

        String lowerPrefix = prefix.toLowerCase();
        return categoryEntities.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerPrefix))
                .collect(Collectors.toList());
    }

    /**
     * Get all available entity categories
     */
    public static List<String> getEntityCategories(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return new ArrayList<>(ENTITY_CATEGORIES.keySet());
        }

        String lowerPrefix = prefix.toLowerCase();
        return ENTITY_CATEGORIES.keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(lowerPrefix))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get common biome names
     */
    public static List<String> getCommonBiomes(String prefix) {
        List<String> biomes = Arrays.asList(
            // Basic biomes
            "PLAINS", "DESERT", "FOREST", "TAIGA", "SWAMP", "JUNGLE",
            "MOUNTAINS", "OCEAN", "BEACH", "RIVER",

            // Variants
            "SUNFLOWER_PLAINS", "FLOWER_FOREST", "BIRCH_FOREST", "DARK_FOREST",
            "OLD_GROWTH_FOREST", "SNOWY_TAIGA", "GIANT_TAIGA", "MUSHROOM_FIELDS",

            // Climate variants
            "BADLANDS", "WOODED_BADLANDS", "ICE_SPIKES", "SNOWY_PLAINS",
            "FROZEN_OCEAN", "COLD_OCEAN", "LUKEWARM_OCEAN", "WARM_OCEAN",

            // Mountain variants
            "WINDSWEPT_HILLS", "WINDSWEPT_FOREST", "WINDSWEPT_SAVANNA",
            "STONY_PEAKS", "FROZEN_PEAKS", "JAGGED_PEAKS",

            // Ocean variants
            "DEEP_OCEAN", "DEEP_COLD_OCEAN", "DEEP_LUKEWARM_OCEAN",
            "DEEP_FROZEN_OCEAN", "DEEP_WARM_OCEAN"
        );

        return filterStartingWith(biomes, prefix);
    }

    /**
     * Get permission suggestions based on sender
     */
    public static List<String> getPermissionSuggestions(CommandSender sender, String prefix) {
        List<String> suggestions = new ArrayList<>();

        if (sender.isOp() || sender.hasPermission("realmobscale.admin")) {
            suggestions.addAll(Arrays.asList(
                "realmobscale.*",
                "realmobscale.admin",
                "realmobscale.user"
            ));
        }

        if (sender.hasPermission("realmobscale.user") || sender.isOp()) {
            suggestions.add("realmobscale.user");
        }

        return filterStartingWith(suggestions, prefix);
    }

    /**
     * Get boolean suggestions (true/false)
     */
    public static List<String> getBooleanSuggestions(String prefix) {
        List<String> booleans = Arrays.asList("true", "false", "yes", "no", "on", "off", "enable", "disable");
        return filterStartingWith(booleans, prefix);
    }

    /**
     * Get world names from the server
     */
    public static List<String> getWorldNames(CommandSender sender, String prefix) {
        if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
            // Console can see all worlds
            return sender.getServer().getWorlds().stream()
                    .map(org.bukkit.World::getName)
                    .filter(name -> prefix == null || prefix.isEmpty() || name.toLowerCase().startsWith(prefix.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            // Players can only see worlds they have access to
            return sender.getServer().getWorlds().stream()
                    .filter(world -> sender.hasPermission("realmobscale.admin") || sender.hasPermission("realmobscale.world." + world.getName()))
                    .map(org.bukkit.World::getName)
                    .filter(name -> prefix == null || prefix.isEmpty() || name.toLowerCase().startsWith(prefix.toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get player names from the server
     */
    public static List<String> getPlayerNames(String prefix) {
        return org.bukkit.Bukkit.getOnlinePlayers().stream()
                .map(player -> player.getName())
                .filter(name -> prefix == null || prefix.isEmpty() || name.toLowerCase().startsWith(prefix.toLowerCase()))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get time unit suggestions
     */
    public static List<String> getTimeUnits(String prefix) {
        List<String> units = Arrays.asList("ticks", "seconds", "minutes", "hours", "days");
        return filterStartingWith(units, prefix);
    }

    /**
     * Get size category suggestions
     */
    public static List<String> getSizeCategories(String prefix) {
        List<String> categories = Arrays.asList(
            "tiny", "small", "medium", "large", "huge", "giant",
            "microscopic", "miniature", "normal", "massive", "colossal"
        );
        return filterStartingWith(categories, prefix);
    }
}