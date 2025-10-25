package com.minekarta.realMobScale.data;

import org.bukkit.entity.EntityType;
import java.util.HashMap;
import java.util.Map;

public class MobData {
    private static final Map<EntityType, ScaleProfile> SCALE_REGISTRY = new HashMap<>();

    static {
        initializeScaleData();
    }

    private static void initializeScaleData() {
        // =======================================================================
        // ACCURATE REAL-WORLD SCALING - MINECRAFT VS REALITY COMPARISON
        // =======================================================================
        // Minecraft scale reference: 1 block = 1 meter
        // Player height: 1.8 blocks = 1.8 meters (this is our reference)
        //
        // Scale factor calculation: RealHeight / MinecraftDefaultHeight
        // Example: Real cow = 1.5m, Minecraft cow = 1.4m, Scale = 1.5/1.4 = 1.07x
        // =======================================================================

        // ==================== DOMESTIC FARM ANIMALS ====================

        // Cattle
        // Minecraft cow: ~1.4 blocks tall, Real Holstein cow: 1.5m shoulder height
        SCALE_REGISTRY.put(EntityType.COW, new ScaleProfile(1.5, 1.07, 0.8, "Holstein cow - 1.5m shoulder height"));
        SCALE_REGISTRY.put(EntityType.MOOSHROOM, new ScaleProfile(1.5, 1.07, 0.8, "Mooshroom cow - 1.5m shoulder height"));

        // Swine
        // Minecraft pig: ~0.9 blocks tall, Real domestic pig: 0.8m shoulder height
        SCALE_REGISTRY.put(EntityType.PIG, new ScaleProfile(0.8, 0.89, 0.9, "Domestic pig - 0.8m shoulder height"));
        // Minecraft hoglin: ~1.4 blocks tall, Real wild boar: 1.2m shoulder height
        SCALE_REGISTRY.put(EntityType.HOGLIN, new ScaleProfile(1.2, 0.86, 0.9, "Wild boar - 1.2m shoulder height"));
        SCALE_REGISTRY.put(EntityType.ZOGLIN, new ScaleProfile(1.2, 0.86, 0.9, "Zombified hoglin - 1.2m shoulder height"));

        // Sheep and Goats
        // Minecraft sheep: ~1.3 blocks tall, Real domestic sheep: 0.9m shoulder height
        SCALE_REGISTRY.put(EntityType.SHEEP, new ScaleProfile(0.9, 0.69, 0.95, "Domestic sheep - 0.9m shoulder height"));
        // Minecraft goat: ~1.2 blocks tall, Real domestic goat: 0.8m shoulder height
        SCALE_REGISTRY.put(EntityType.GOAT, new ScaleProfile(0.8, 0.67, 1.0, "Domestic goat - 0.8m shoulder height"));

        // Poultry
        // Minecraft chicken: ~0.7 blocks tall, Real chicken: 0.35m height
        SCALE_REGISTRY.put(EntityType.CHICKEN, new ScaleProfile(0.35, 0.5, 1.2, "Chicken - 0.35m height"));
        // Minecraft parrot: ~0.9 blocks tall, Real macaw parrot: 0.25m height
        SCALE_REGISTRY.put(EntityType.PARROT, new ScaleProfile(0.25, 0.28, 1.2, "Macaw parrot - 0.25m height"));

        // Equines
        // Minecraft horse: ~1.6 blocks tall, Real horse: 1.6m shoulder height
        SCALE_REGISTRY.put(EntityType.HORSE, new ScaleProfile(1.6, 1.0, 0.7, "Horse - 1.6m shoulder height"));
        // Minecraft donkey: ~1.3 blocks tall, Real donkey: 1.2m shoulder height
        SCALE_REGISTRY.put(EntityType.DONKEY, new ScaleProfile(1.2, 0.92, 0.8, "Donkey - 1.2m shoulder height"));
        // Minecraft mule: ~1.5 blocks tall, Real mule: 1.4m shoulder height
        SCALE_REGISTRY.put(EntityType.MULE, new ScaleProfile(1.4, 0.93, 0.75, "Mule - 1.4m shoulder height"));
        SCALE_REGISTRY.put(EntityType.SKELETON_HORSE, new ScaleProfile(1.6, 1.0, 0.7, "Skeleton horse - 1.6m shoulder height"));
        SCALE_REGISTRY.put(EntityType.ZOMBIE_HORSE, new ScaleProfile(1.6, 1.0, 0.7, "Zombie horse - 1.6m shoulder height"));

        // Camelids
        // Minecraft llama: ~1.8 blocks tall, Real llama: 1.8m shoulder height
        SCALE_REGISTRY.put(EntityType.LLAMA, new ScaleProfile(1.8, 1.0, 0.85, "Llama - 1.8m shoulder height"));
        SCALE_REGISTRY.put(EntityType.TRADER_LLAMA, new ScaleProfile(1.8, 1.0, 0.85, "Trader llama - 1.8m shoulder height"));
        // Minecraft camel: ~2.2 blocks tall, Real dromedary camel: 2.1m shoulder height
        SCALE_REGISTRY.put(EntityType.CAMEL, new ScaleProfile(2.1, 0.95, 0.6, "Dromedary camel - 2.1m shoulder height"));

        // ==================== WILD ANIMALS ====================

        // Bears
        // Minecraft polar bear: ~1.4 blocks tall, Real polar bear: 1.5m shoulder height
        SCALE_REGISTRY.put(EntityType.POLAR_BEAR, new ScaleProfile(1.5, 1.07, 0.8, "Polar bear - 1.5m shoulder height"));

        // Canines
        // Minecraft wolf: ~0.85 blocks tall, Real gray wolf: 0.8m shoulder height
        SCALE_REGISTRY.put(EntityType.WOLF, new ScaleProfile(0.8, 0.94, 1.1, "Gray wolf - 0.8m shoulder height"));
        // Minecraft fox: ~0.7 blocks tall, Real red fox: 0.4m shoulder height
        SCALE_REGISTRY.put(EntityType.FOX, new ScaleProfile(0.4, 0.57, 1.1, "Red fox - 0.4m shoulder height"));

        // Felines
        // Minecraft ocelot: ~0.7 blocks tall, Real ocelot: 0.5m shoulder height
        SCALE_REGISTRY.put(EntityType.OCELOT, new ScaleProfile(0.5, 0.71, 1.3, "Ocelot - 0.5m shoulder height"));
        // Minecraft cat: ~0.6 blocks tall, Real domestic cat: 0.5m height (ideal for gameplay visibility)
        SCALE_REGISTRY.put(EntityType.CAT, new ScaleProfile(0.5, 0.83, 1.3, "Domestic cat - 0.5m height"));

        // Bears (continued)
        // Minecraft panda: ~1.2 blocks tall, Real giant panda: 1.0m shoulder height
        SCALE_REGISTRY.put(EntityType.PANDA, new ScaleProfile(1.0, 0.83, 0.9, "Giant panda - 1.0m shoulder height"));

        // Small Mammals
        // Minecraft rabbit: ~0.5 blocks tall, Real European rabbit: 0.3m height
        SCALE_REGISTRY.put(EntityType.RABBIT, new ScaleProfile(0.3, 0.6, 1.4, "European rabbit - 0.3m height"));

        // ==================== AQUATIC ANIMALS ====================

        // Marine Mammals
        // Minecraft dolphin: ~0.6 blocks tall, Real bottlenose dolphin: 2.4m length
        SCALE_REGISTRY.put(EntityType.DOLPHIN, new ScaleProfile(2.4, 4.0, 0.8, "Bottlenose dolphin - 2.4m length"));

        // Fish
        // Minecraft cod: ~0.6 blocks tall, Real Atlantic cod: 1.2m length
        SCALE_REGISTRY.put(EntityType.COD, new ScaleProfile(1.2, 2.0, 1.1, "Atlantic cod - 1.2m length"));
        // Minecraft salmon: ~0.7 blocks tall, Real Atlantic salmon: 1.5m length
        SCALE_REGISTRY.put(EntityType.SALMON, new ScaleProfile(1.5, 2.14, 1.0, "Atlantic salmon - 1.5m length"));
        // Minecraft pufferfish: ~0.7 blocks tall, Real pufferfish: 0.5m inflated diameter
        SCALE_REGISTRY.put(EntityType.PUFFERFISH, new ScaleProfile(0.5, 0.71, 2.2, "Pufferfish - 0.5m inflated diameter"));
        // Minecraft tropical fish: ~0.3 blocks tall, Real tropical fish: 0.15m length
        SCALE_REGISTRY.put(EntityType.TROPICAL_FISH, new ScaleProfile(0.15, 0.5, 1.7, "Tropical fish - 0.15m length"));

        // Cephalopods
        // Minecraft squid: ~0.8 blocks tall, Real giant squid: 0.5m mantle length
        SCALE_REGISTRY.put(EntityType.SQUID, new ScaleProfile(0.5, 0.63, 1.3, "Giant squid - 0.5m mantle length"));
        // Minecraft glow squid: ~0.8 blocks tall, Real firefly squid: 0.3m mantle length
        SCALE_REGISTRY.put(EntityType.GLOW_SQUID, new ScaleProfile(0.3, 0.38, 1.3, "Firefly squid - 0.3m mantle length"));

        // ==================== AMPHIBIANS AND REPTILES ====================

        // Minecraft frog: ~0.5 blocks tall, Real bullfrog: 0.08m body length
        SCALE_REGISTRY.put(EntityType.FROG, new ScaleProfile(0.08, 0.16, 1.5, "Bullfrog - 0.08m body length"));
        // Minecraft turtle: ~0.6 blocks tall, Real sea turtle: 1.5m shell length
        SCALE_REGISTRY.put(EntityType.TURTLE, new ScaleProfile(1.5, 2.5, 1.0, "Sea turtle - 1.5m shell length"));
        // Minecraft axolotl: ~0.4 blocks tall, Real axolotl: 0.15m length
        SCALE_REGISTRY.put(EntityType.AXOLOTL, new ScaleProfile(0.15, 0.38, 1.2, "Axolotl - 0.15m length"));

        // ==================== INSECTS AND ARTHROPODS ====================

        // Minecraft bee: ~0.6 blocks tall, Real honeybee: 0.015m length
        // Increased scale for gameplay visibility while maintaining relative size
        SCALE_REGISTRY.put(EntityType.BEE, new ScaleProfile(0.015, 0.15, 2.0, "Honeybee - 0.015m length (scaled for visibility)"));
        // Minecraft spider: ~0.9 blocks tall, Real tarantula: 0.05m body length
        SCALE_REGISTRY.put(EntityType.SPIDER, new ScaleProfile(0.05, 0.056, 1.5, "Tarantula - 0.05m body length"));
        // Minecraft cave spider: ~0.5 blocks tall, Real small spider: 0.03m body length
        SCALE_REGISTRY.put(EntityType.CAVE_SPIDER, new ScaleProfile(0.03, 0.06, 1.6, "Cave spider - 0.03m body length"));
        // Minecraft silverfish: ~0.3 blocks tall, Real silverfish: 0.02m length
        SCALE_REGISTRY.put(EntityType.SILVERFISH, new ScaleProfile(0.02, 0.067, 3.5, "Silverfish - 0.02m length"));
        // Minecraft endermite: ~0.3 blocks tall, Real endermite: 0.02m length
        SCALE_REGISTRY.put(EntityType.ENDERMITE, new ScaleProfile(0.02, 0.067, 3.0, "Endermite - 0.02m length"));

        // ==================== FANTASY CREATURES ====================

        // Flying Creatures
        // Minecraft bat: ~0.9 blocks tall, Real fruit bat: 0.08m body length
        SCALE_REGISTRY.put(EntityType.BAT, new ScaleProfile(0.08, 0.089, 1.8, "Fruit bat - 0.08m body length"));
        // Minecraft phantom: ~0.5 blocks tall (wingspan large), Real large flying creature: 1.2m wingspan
        SCALE_REGISTRY.put(EntityType.PHANTOM, new ScaleProfile(1.2, 2.4, 1.5, "Phantom - 1.2m wingspan"));
        // Minecraft blaze: ~1.8 blocks tall, Real blaze concept: 1.8m height
        SCALE_REGISTRY.put(EntityType.BLAZE, new ScaleProfile(1.8, 1.0, 0.9, "Blaze - 1.8m height"));

        // Small Fantasy Creatures
        // Minecraft allay: ~0.6 blocks tall, Real fairy concept: 0.06m height
        SCALE_REGISTRY.put(EntityType.ALLAY, new ScaleProfile(0.06, 0.1, 3.0, "Allay - 0.06m fairy creature"));

        // Slime Creatures
        // Minecraft slime: variable size, Real concept: 0.5m diameter
        SCALE_REGISTRY.put(EntityType.SLIME, new ScaleProfile(0.5, 0.83, 2.0, "Slime - 0.5m diameter"));
        // Minecraft magma cube: variable size, Real concept: 0.6m diameter
        SCALE_REGISTRY.put(EntityType.MAGMA_CUBE, new ScaleProfile(0.6, 1.0, 1.8, "Magma cube - 0.6m diameter"));

        // Nether Creatures
        // Minecraft strider: ~2.0 blocks tall, Real concept: 2.5m height to head
        SCALE_REGISTRY.put(EntityType.STRIDER, new ScaleProfile(2.5, 1.25, 0.9, "Strider - 2.5m height to head"));

        // ==================== HUMANOID CREATURES ====================

        // Human-like creatures (Minecraft humanoids: ~1.8 blocks = 1.8m reference)
        SCALE_REGISTRY.put(EntityType.ZOMBIE, new ScaleProfile(1.8, 1.0, 1.0, "Zombie - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.DROWNED, new ScaleProfile(1.8, 1.0, 1.0, "Drowned - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.HUSK, new ScaleProfile(1.8, 1.0, 1.0, "Husk - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.ZOMBIE_VILLAGER, new ScaleProfile(1.8, 1.0, 1.0, "Zombie villager - 1.8m height"));

        // Skeletons
        SCALE_REGISTRY.put(EntityType.SKELETON, new ScaleProfile(1.8, 1.0, 1.0, "Skeleton - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.STRAY, new ScaleProfile(1.8, 1.0, 1.0, "Stray - 1.8m height"));
        // Minecraft wither skeleton: ~2.4 blocks tall, Real concept: 2.4m height
        SCALE_REGISTRY.put(EntityType.WITHER_SKELETON, new ScaleProfile(2.4, 1.0, 0.7, "Wither skeleton - 2.4m height"));

        // Humans and Villagers
        SCALE_REGISTRY.put(EntityType.VILLAGER, new ScaleProfile(1.75, 0.97, 1.0, "Villager - 1.75m average height"));
        SCALE_REGISTRY.put(EntityType.WANDERING_TRADER, new ScaleProfile(1.75, 0.97, 1.0, "Wandering trader - 1.75m height"));
        SCALE_REGISTRY.put(EntityType.WITCH, new ScaleProfile(1.7, 0.94, 1.0, "Witch - 1.7m height"));

        // Illagers
        SCALE_REGISTRY.put(EntityType.PILLAGER, new ScaleProfile(1.8, 1.0, 1.0, "Pillager - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.VINDICATOR, new ScaleProfile(1.8, 1.0, 1.0, "Vindicator - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.EVOKER, new ScaleProfile(1.8, 1.0, 1.0, "Evoker - 1.8m height"));
        SCALE_REGISTRY.put(EntityType.ILLUSIONER, new ScaleProfile(1.8, 1.0, 1.0, "Illusioner - 1.8m height"));

        // Piglin variants
        SCALE_REGISTRY.put(EntityType.PIGLIN, new ScaleProfile(1.85, 1.03, 1.0, "Piglin - 1.85m height"));
        SCALE_REGISTRY.put(EntityType.PIGLIN_BRUTE, new ScaleProfile(2.0, 1.11, 0.9, "Piglin brute - 2.0m height"));
        SCALE_REGISTRY.put(EntityType.ZOMBIFIED_PIGLIN, new ScaleProfile(1.85, 1.03, 1.0, "Zombified piglin - 1.85m height"));

        // Other Humanoids
        SCALE_REGISTRY.put(EntityType.PLAYER, new ScaleProfile(1.8, 1.0, 1.0, "Player reference - 1.8m height"));

        // ==================== LARGE MONSTERS ====================

        // Tall Creatures
        SCALE_REGISTRY.put(EntityType.CREEPER, new ScaleProfile(1.7, 0.94, 1.1, "Creeper - 1.7m height"));
        SCALE_REGISTRY.put(EntityType.ENDERMAN, new ScaleProfile(2.9, 1.0, 0.7, "Enderman - 2.9m height"));
        SCALE_REGISTRY.put(EntityType.IRON_GOLEM, new ScaleProfile(2.7, 0.75, 0.5, "Iron golem - 2.7m height"));
        SCALE_REGISTRY.put(EntityType.SHULKER, new ScaleProfile(1.0, 1.0, 0.4, "Shulker - 1.0m cube"));

        // Large Hostile Creatures
        SCALE_REGISTRY.put(EntityType.RAVAGER, new ScaleProfile(2.2, 1.0, 0.8, "Ravager - 2.2m length"));
        SCALE_REGISTRY.put(EntityType.WARDEN, new ScaleProfile(3.0, 1.0, 0.6, "Warden - 3.0m height"));

        // Nether Monsters
        SCALE_REGISTRY.put(EntityType.GHAST, new ScaleProfile(4.0, 2.67, 0.5, "Ghast - 4.0m width"));
        SCALE_REGISTRY.put(EntityType.WITHER, new ScaleProfile(3.5, 0.97, 0.4, "Wither - 3.5m height"));

        // ==================== BOSS CREATURES ====================

        SCALE_REGISTRY.put(EntityType.ENDER_DRAGON, new ScaleProfile(8.0, 2.67, 0.2, "Ender dragon - 8.0m length"));

        // ==================== SPECIAL CREATURES ====================

        // Unique Minecraft creatures
        SCALE_REGISTRY.put(EntityType.GUARDIAN, new ScaleProfile(1.5, 1.0, 0.8, "Guardian - 1.5m length"));
        SCALE_REGISTRY.put(EntityType.ELDER_GUARDIAN, new ScaleProfile(3.0, 2.0, 0.4, "Elder guardian - 3.0m length"));

        // Special passive creatures
        SCALE_REGISTRY.put(EntityType.SNOW_GOLEM, new ScaleProfile(1.2, 0.8, 1.2, "Snow golem - 1.2m height"));
    }
    
    public static ScaleProfile getScaleProfile(EntityType type) {
        return SCALE_REGISTRY.get(type);
    }
    
    public static boolean hasScaleData(EntityType type) {
        return SCALE_REGISTRY.containsKey(type);
    }
}