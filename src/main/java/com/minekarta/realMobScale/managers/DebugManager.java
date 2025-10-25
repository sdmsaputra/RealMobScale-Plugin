package com.minekarta.realMobScale.managers;

import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages debug mode for administrators
 * Allows admins to toggle debug mode and receive scaling information
 */
public class DebugManager {

    private final Set<UUID> debugPlayers = new HashSet<>();

    /**
     * Toggle debug mode for a player
     * @param player The player to toggle debug mode for
     * @return true if debug mode is now enabled, false if disabled
     */
    public boolean toggleDebugMode(Player player) {
        UUID playerId = player.getUniqueId();

        if (debugPlayers.contains(playerId)) {
            debugPlayers.remove(playerId);
            return false;
        } else {
            debugPlayers.add(playerId);
            return true;
        }
    }

    /**
     * Check if a player has debug mode enabled
     * @param player The player to check
     * @return true if debug mode is enabled
     */
    public boolean isDebugMode(Player player) {
        return debugPlayers.contains(player.getUniqueId());
    }

    /**
     * Send debug message to all players with debug mode enabled
     * @param message The debug message to send
     */
    public void sendDebugMessage(String message) {
        if (debugPlayers.isEmpty()) {
            return;
        }

        String formattedMessage = "§8[§bDEBUG§8]§7 " + message;

        for (UUID playerId : debugPlayers) {
            Player player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    /**
     * Send debug message to all players with debug mode enabled with context
     * @param message The debug message to send
     * @param entityName The name of the entity being scaled
     * @param scaleFactor The scale factor applied
     * @param location The location where scaling occurred
     */
    public void sendScalingDebugMessage(String message, String entityName, double scaleFactor, org.bukkit.Location location) {
        if (debugPlayers.isEmpty()) {
            return;
        }

        String formattedMessage = String.format(
            "§8[§bDEBUG§8]§7 %s §8|§7 Entity: §e%s §8|§7 Scale: §a%.2fx §8|§7 Location: §d%.1f,%.1f,%.1f",
            message, entityName, scaleFactor, location.getX(), location.getY(), location.getZ()
        );

        for (UUID playerId : debugPlayers) {
            Player player = org.bukkit.Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                player.sendMessage(formattedMessage);
            }
        }
    }

    /**
     * Remove player from debug mode (called when they leave the server)
     * @param playerId The UUID of the player to remove
     */
    public void removeDebugPlayer(UUID playerId) {
        debugPlayers.remove(playerId);
    }

    /**
     * Get the number of players currently in debug mode
     * @return The count of debug players
     */
    public int getDebugPlayerCount() {
        return debugPlayers.size();
    }

    /**
     * Get all players currently in debug mode
     * @return Set of player UUIDs in debug mode
     */
    public Set<UUID> getDebugPlayers() {
        return new HashSet<>(debugPlayers);
    }
}