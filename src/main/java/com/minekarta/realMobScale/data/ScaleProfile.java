package com.minekarta.realMobScale.data;

public class ScaleProfile {
    private final double realWorldHeight; // in meters (actual measurement)
    private final double scaleFactor; // Minecraft scale factor
    private final double healthMultiplier; // Health scaling based on size
    private final double damageMultiplier; // Damage scaling based on size
    private final double speedMultiplier; // Speed adjustment based on size
    private final double babyScaleFactor; // Scale factor for baby variants
    private final String description; // Description of the real-world animal

    public ScaleProfile(double realWorldHeight, double scaleFactor, double speedMultiplier) {
        this(realWorldHeight, scaleFactor, speedMultiplier, "Custom creature");
    }

    public ScaleProfile(double realWorldHeight, double scaleFactor, double speedMultiplier, String description) {
        this.realWorldHeight = realWorldHeight;
        this.scaleFactor = scaleFactor;
        this.healthMultiplier = calculateHealthMultiplier(realWorldHeight);
        this.damageMultiplier = calculateDamageMultiplier(realWorldHeight);
        this.speedMultiplier = speedMultiplier;
        this.babyScaleFactor = calculateBabyScaleFactor();
        this.description = description;
    }

    /**
     * Calculate health multiplier based on square-cube law
     * Larger animals have proportionally more mass and health capacity
     */
    private double calculateHealthMultiplier(double height) {
        // Using biomechanical scaling laws
        // Health scales with body mass, which scales with height^3
        // But we use exponent 0.7 to keep it balanced for gameplay
        double normalizedHeight = height / 1.8; // Normalize to human height (1.8m)
        return Math.max(0.1, Math.pow(normalizedHeight, 0.7));
    }

    /**
     * Calculate damage multiplier based on size and strength
     * Larger animals can hit harder but not proportionally to their size
     */
    private double calculateDamageMultiplier(double height) {
        double normalizedHeight = height / 1.8; // Normalize to human height
        return Math.max(0.2, Math.pow(normalizedHeight, 0.5));
    }

    /**
     * Calculate baby scale factor based on adult size
     * Updated proportions for better visibility while remaining realistic
     * Babies are typically 50-80% of adult size depending on species
     */
    private double calculateBabyScaleFactor() {
        // Adjusted proportions for better game visibility
        // Still realistic but more visible and cute
        if (realWorldHeight < 0.1) return 0.8; // Very small animals (insects, fish)
        if (realWorldHeight < 0.5) return 0.7; // Small animals (rabbits, chickens, cats)
        if (realWorldHeight < 1.0) return 0.6; // Medium-small animals (foxes, wolves)
        if (realWorldHeight < 2.0) return 0.55; // Medium animals (sheep, pigs, goats)
        return 0.6; // Large animals (horses, cows, bears, camels) - increased for better visibility
    }

    /**
     * Get adjusted scale factor for baby animals
     */
    public double getBabyAdjustedScale() {
        return scaleFactor * babyScaleFactor;
    }

    /**
     * Get adjusted scale factor for baby animals with global multiplier
     */
    public double getBabyAdjustedScale(double globalMultiplier) {
        return scaleFactor * babyScaleFactor * globalMultiplier;
    }

    /**
     * Check if this creature is considered giant (over 3m)
     */
    public boolean isGiant() {
        return realWorldHeight > 3.0;
    }

    /**
     * Check if this creature is considered tiny (under 0.1m)
     */
    public boolean isTiny() {
        return realWorldHeight < 0.1;
    }

    /**
     * Get size category for logging/display purposes
     */
    public String getSizeCategory() {
        if (isTiny()) return "Microscopic";
        if (realWorldHeight < 0.3) return "Tiny";
        if (realWorldHeight < 1.0) return "Small";
        if (realWorldHeight < 2.0) return "Medium";
        if (realWorldHeight < 4.0) return "Large";
        return "Giant";
    }

    // ==================== GETTERS ====================

    public double getRealWorldHeight() {
        return realWorldHeight;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    public double getHealthMultiplier() {
        return healthMultiplier;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public double getBabyScaleFactor() {
        return babyScaleFactor;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get formatted information about this scale profile
     */
    public String getDetailedInfo() {
        return String.format("%s (%.2fm, %s) - Scale: %.2fx, Health: %.2fx, Speed: %.2fx",
            description, realWorldHeight, getSizeCategory(), scaleFactor, healthMultiplier, speedMultiplier);
    }
}