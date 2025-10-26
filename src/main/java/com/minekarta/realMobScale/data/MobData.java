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

        // Swine
        // Minecraft pig: ~0.9 blocks tall, Real domestic pig: 0.8m shoulder height
        SCALE_REGISTRY.put(EntityType.PIG, new ScaleProfile(0.8, 0.89, 0.9, "Domestic pig - 0.8m shoulder height"));

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

        // ==================== BATS (FLYING MAMMALS) ====================

        // Minecraft bat: ~0.9 blocks tall, Real large fruit bat: 0.25m wingspan (adjusted for visibility)
        SCALE_REGISTRY.put(EntityType.BAT, new ScaleProfile(0.25, 0.28, 1.8, "Large fruit bat - 0.25m wingspan (scaled for visibility)"));
    }
    
    public static ScaleProfile getScaleProfile(EntityType type) {
        return SCALE_REGISTRY.get(type);
    }
    
    public static boolean hasScaleData(EntityType type) {
        return SCALE_REGISTRY.containsKey(type);
    }
}