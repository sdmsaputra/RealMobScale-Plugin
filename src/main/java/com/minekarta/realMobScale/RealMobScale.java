package com.minekarta.realMobScale;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import com.minekarta.realMobScale.managers.MobScaleManager;
import com.minekarta.realMobScale.managers.ConfigManager;
import com.minekarta.realMobScale.managers.PlayerPreferencesManager;
import com.minekarta.realMobScale.managers.BiomeScalingManager;
import com.minekarta.realMobScale.managers.DebugManager;
import com.minekarta.realMobScale.events.MobSpawnListener;
import com.minekarta.realMobScale.commands.ScaleCommand;
import com.minekarta.realMobScale.commands.PreferencesCommand;
import com.minekarta.realMobScale.packets.EntityMetadataPacketHandler;
import com.minekarta.realMobScale.packets.EntitySpawnPacketHandler;
import com.github.retrooper.packetevents.PacketEvents;

public class RealMobScale extends JavaPlugin implements Listener {
    private static RealMobScale instance;
    private MobScaleManager mobScaleManager;
    private ConfigManager configManager;
    private PlayerPreferencesManager playerPreferencesManager;
    private BiomeScalingManager biomeScalingManager;
    private DebugManager debugManager;
    private EntityMetadataPacketHandler metadataHandler;
    private EntitySpawnPacketHandler spawnHandler;
    
    @Override
    public void onEnable() {
        instance = this;

        // Initialize PacketEvents first
        try {
            PacketEvents.getAPI().load();
            getLogger().info("PacketEvents loaded successfully!");
        } catch (Exception e) {
            getLogger().severe("Failed to load PacketEvents: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.mobScaleManager = new MobScaleManager();
        this.playerPreferencesManager = new PlayerPreferencesManager(this);
        this.biomeScalingManager = new BiomeScalingManager(this, configManager);
        this.debugManager = new DebugManager();

        // Initialize packet handlers
        this.metadataHandler = new EntityMetadataPacketHandler(this);
        this.spawnHandler = new EntitySpawnPacketHandler(this, metadataHandler);

        // Register packet listeners
        PacketEvents.getAPI().getEventManager().registerListener(metadataHandler);
        PacketEvents.getAPI().getEventManager().registerListener(spawnHandler);

        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Register events
        Bukkit.getPluginManager().registerEvents(new MobSpawnListener(), this);
        Bukkit.getPluginManager().registerEvents(this, this);

        // Register commands
        getCommand("realmobscale").setExecutor(new ScaleCommand());
        getCommand("prefs").setExecutor(new PreferencesCommand());
        getCommand("preferences").setExecutor(new PreferencesCommand());

        getLogger().info("RealMobScale v" + getDescription().getVersion() + " activated!");
        getLogger().info("PacketEvents integration enabled with smooth visual scaling!");
        getLogger().info("Player preferences system initialized!");
        getLogger().info("Biome-specific scaling enabled with " + biomeScalingManager.getConfiguredBiomeCount() + " biome configurations!");
    }
  
    @Override
    public void onDisable() {
        // Save player preferences
        if (playerPreferencesManager != null) {
            playerPreferencesManager.savePreferences();
        }

        // Clean up packet handlers
        if (metadataHandler != null) {
            metadataHandler.clearAllEntities();
        }

        // Clean up PacketEvents
        if (PacketEvents.getAPI().isLoaded()) {
            PacketEvents.getAPI().terminate();
        }
        getLogger().info("RealMobScale disabled!");
    }
    
    public static RealMobScale getInstance() {
        return instance;
    }
    
    public MobScaleManager getMobScaleManager() {
        return mobScaleManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public EntityMetadataPacketHandler getMetadataHandler() {
        return metadataHandler;
    }

    public PlayerPreferencesManager getPlayerPreferencesManager() {
        return playerPreferencesManager;
    }

    public BiomeScalingManager getBiomeScalingManager() {
        return biomeScalingManager;
    }

    public DebugManager getDebugManager() {
        return debugManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player from debug mode when they leave
        debugManager.removeDebugPlayer(event.getPlayer().getUniqueId());
    }
}