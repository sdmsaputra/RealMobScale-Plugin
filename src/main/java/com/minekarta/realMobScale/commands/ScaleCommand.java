package com.minekarta.realMobScale.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.minekarta.realMobScale.RealMobScale;
import com.minekarta.realMobScale.events.MobScaleConfigReloadEvent;
import com.minekarta.realMobScale.utils.TabCompletionUtils;
import java.util.*;
import java.util.stream.Collectors;

public class ScaleCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("realmobscale.admin")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }

                // Store old settings for comparison
                Map<String, Object> oldSettings = new HashMap<>();
                Map<String, Object> newSettings = new HashMap<>();

                // Capture some key settings that might have changed
                oldSettings.put("enabled", RealMobScale.getInstance().getConfigManager().isEnabled());
                oldSettings.put("debug", RealMobScale.getInstance().getConfigManager().isDebug());

                // Reload configuration
                RealMobScale.getInstance().getConfigManager().reloadConfig();
                RealMobScale.getInstance().getBiomeScalingManager().reloadBiomeConfig();

                // Capture new settings
                newSettings.put("enabled", RealMobScale.getInstance().getConfigManager().isEnabled());
                newSettings.put("debug", RealMobScale.getInstance().getConfigManager().isDebug());

                // Fire configuration reload event
                MobScaleConfigReloadEvent reloadEvent = new MobScaleConfigReloadEvent(
                    sender instanceof Player ? ((Player) sender).getName() : "CONSOLE",
                    newSettings
                );
                RealMobScale.getInstance().getServer().getPluginManager().callEvent(reloadEvent);

                sender.sendMessage("§aRealMobScale configuration reloaded!");
                sender.sendMessage("§7Biome configurations: §f" + RealMobScale.getInstance().getBiomeScalingManager().getConfiguredBiomeCount() + " biomes");
                if (RealMobScale.getInstance().getConfigManager().isDebugMode()) {
                    sender.sendMessage("§7Debug mode: §f" + (RealMobScale.getInstance().getConfigManager().isDebug() ? "enabled" : "disabled"));
                }
                break;

            case "info":
                sendPluginInfo(sender);
                break;

            case "apply":
                if (!sender.hasPermission("realmobscale.admin")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                handleApplyCommand(sender, args);
                break;

            case "stats":
                if (!sender.hasPermission("realmobscale.admin")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                sendStatistics(sender);
                break;

            case "debug":
                if (!sender.hasPermission("realmobscale.debug")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                handleDebugCommand(sender, args);
                break;

            case "toggle":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players!");
                    return true;
                }
                if (!sender.hasPermission("realmobscale.user")) {
                    sender.sendMessage("§cYou don't have permission to use this command!");
                    return true;
                }
                sender.sendMessage("§aUse §f/prefs toggle §afor player preferences!");
                break;

            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== RealMobScale Commands ===");
        sender.sendMessage("§f/realmobscale reload §7- Reload configuration");
        sender.sendMessage("§f/realmobscale info §7- Show plugin information");
        sender.sendMessage("§f/realmobscale stats §7- Show scaling statistics");
        sender.sendMessage("§f/realmobscale apply §7- Apply scaling to entities");
        sender.sendMessage("§7  - apply all §7- Apply to all existing entities");
        sender.sendMessage("§7  - apply world <world> §7- Apply to specific world");
        sender.sendMessage("§7  - apply radius <blocks> §7- Apply in radius");

        if (sender.hasPermission("realmobscale.debug")) {
            sender.sendMessage("§f/realmobscale debug §7- Toggle debug mode");
            sender.sendMessage("§7  - debug on/off §7- Enable/disable debug messages");
        }

        sender.sendMessage("§f/prefs toggle §7- Toggle scaling visibility");
    }
    
    private void sendPluginInfo(CommandSender sender) {
        sender.sendMessage("§6=== RealMobScale Information ===");
        sender.sendMessage("§fVersion: §7" + RealMobScale.getInstance().getDescription().getVersion());
        sender.sendMessage("§fAuthor: §7" + String.join(", ", RealMobScale.getInstance().getDescription().getAuthors()));
        sender.sendMessage("§fDescription: §7" + RealMobScale.getInstance().getDescription().getDescription());
        sender.sendMessage("§fPacketEvents: §a" + (RealMobScale.getInstance().getMetadataHandler() != null ? "Integrated" : "Not Available"));
        sender.sendMessage("§fBiome Scaling: §a" + (RealMobScale.getInstance().getBiomeScalingManager().isBiomeScalingEnabled() ? "Enabled" : "Disabled"));
        sender.sendMessage("§fBiome Configs: §7" + RealMobScale.getInstance().getBiomeScalingManager().getConfiguredBiomeCount() + " biomes");
        sender.sendMessage("§fPlayer Prefs: §7" + RealMobScale.getInstance().getPlayerPreferencesManager().getPreferencesCount() + " players");
        sender.sendMessage("§fDebug Players: §7" + RealMobScale.getInstance().getDebugManager().getDebugPlayerCount() + " admins");
        sender.sendMessage("§fTracked Entities: §7" + (RealMobScale.getInstance().getMetadataHandler() != null ?
            RealMobScale.getInstance().getMetadataHandler().getTrackedEntityCount() : "0"));
    }

    private void handleApplyCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /realmobscale apply <all|world|radius> [parameters]");
            return;
        }

        String target = args[1].toLowerCase();
        long startTime = System.currentTimeMillis();

        switch (target) {
            case "all":
                sender.sendMessage("§6Applying scaling to all existing entities...");
                RealMobScale.getInstance().getMobScaleManager().applyScalingToExistingEntities();
                long allTime = System.currentTimeMillis() - startTime;
                sender.sendMessage("§aApplied scaling to all entities! §7(Took " + allTime + "ms)");
                break;

            case "world":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /realmobscale apply world <world_name>");
                    return;
                }
                String worldName = args[2];
                sender.sendMessage("§6Applying scaling to entities in world '" + worldName + "'...");
                int worldCount = RealMobScale.getInstance().getMobScaleManager().applyScalingToWorld(worldName);
                long worldTime = System.currentTimeMillis() - startTime;
                if (worldCount > 0) {
                    sender.sendMessage("§aApplied scaling to " + worldCount + " entities in '" + worldName + "'! §7(Took " + worldTime + "ms)");
                } else {
                    sender.sendMessage("§cNo entities were scaled. Check if the world exists or has living entities.");
                }
                break;

            case "radius":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players!");
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /realmobscale apply radius <blocks>");
                    return;
                }
                try {
                    double radius = Double.parseDouble(args[2]);
                    if (radius <= 0 || radius > 1000) {
                        sender.sendMessage("§cRadius must be between 1 and 1000 blocks!");
                        return;
                    }

                    Player player = (Player) sender;
                    org.bukkit.Location center = player.getLocation();
                    sender.sendMessage("§6Applying scaling to entities within " + radius + " blocks...");
                    int radiusCount = RealMobScale.getInstance().getMobScaleManager().applyScalingInRadius(center, radius);
                    long radiusTime = System.currentTimeMillis() - startTime;
                    if (radiusCount > 0) {
                        sender.sendMessage("§aApplied scaling to " + radiusCount + " entities within radius! §7(Took " + radiusTime + "ms)");
                    } else {
                        sender.sendMessage("§cNo entities were scaled within the specified radius.");
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid radius! Please enter a valid number.");
                }
                break;

            default:
                sender.sendMessage("§cInvalid target! Use: all, world, or radius");
                break;
        }
    }

    private void sendStatistics(CommandSender sender) {
        com.minekarta.realMobScale.managers.MobScaleManager.ScalingStatistics stats =
            RealMobScale.getInstance().getMobScaleManager().getScalingStatistics();

        sender.sendMessage("§6=== RealMobScale Statistics ===");
        sender.sendMessage("§fWorlds Checked: §7" + stats.getWorldsChecked());
        sender.sendMessage("§fTotal Entities: §7" + stats.getTotalEntities());
        sender.sendMessage("§fScaled Entities: §a" + stats.getScaledEntities());
        sender.sendMessage("§fScaling Percentage: §e" + String.format("%.1f", stats.getScalingPercentage()) + "%");

        if (stats.getWorldsChecked() == 0) {
            sender.sendMessage("§cNo worlds are enabled for scaling!");
            sender.sendMessage("§7Check your config.yml world settings.");
        } else if (stats.getTotalEntities() == 0) {
            sender.sendMessage("§7No living entities found in enabled worlds.");
        } else if (stats.getScalingPercentage() == 0) {
            sender.sendMessage("§cNo entities are currently scaled!");
            sender.sendMessage("§7Try running §f/realmobscale apply all §7to scale existing entities.");
        } else {
            sender.sendMessage("§aPlugin is actively scaling entities!");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // Main command suggestions
            List<String> subCommands = Arrays.asList("reload", "info", "stats", "apply", "toggle", "debug", "help");
            return TabCompletionUtils.filterStartingWith(subCommands, args[0]);
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "apply":
                return handleApplyTabCompletion(sender, args);

            case "toggle":
                // Toggle only for players
                return (sender instanceof Player) ?
                    Arrays.asList("on", "off", "enable", "disable") :
                    Collections.emptyList();

            case "reload":
            case "info":
            case "stats":
            case "help":
                // These commands don't have additional arguments
                return Collections.emptyList();

            case "debug":
                if (sender.hasPermission("realmobscale.debug")) {
                    return TabCompletionUtils.getBooleanSuggestions(args[1]);
                }
                break;

            default:
                break;
        }

        return Collections.emptyList();
    }

    /**
     * Handle tab completion for the apply command
     */
    private List<String> handleApplyTabCompletion(CommandSender sender, String[] args) {
        if (args.length == 2) {
            // Apply command targets
            List<String> targets = Arrays.asList("all", "world", "radius", "entity", "type", "chunk");
            return TabCompletionUtils.filterStartingWith(targets, args[1]);
        }
        else if (args.length == 3) {
            String target = args[1].toLowerCase();

            switch (target) {
                case "world":
                    // Suggest world names
                    return TabCompletionUtils.getWorldNames(sender, args[2]);

                case "radius":
                    // Only for players, suggest radius values
                    if (sender instanceof Player) {
                        return TabCompletionUtils.COMMON_RADII;
                    }
                    break;

                case "entity":
                    // Suggest entity types
                    return TabCompletionUtils.getEntityTypes(args[2]);

                case "type":
                    // Suggest entity categories
                    return TabCompletionUtils.getEntityCategories(args[2]);

                case "chunk":
                    // Suggest chunk radius values
                    if (sender instanceof Player) {
                        return Arrays.asList("1", "2", "3", "5", "10");
                    }
                    break;

                default:
                    break;
            }
        }
        else if (args.length == 4) {
            String target = args[1].toLowerCase();

            if (target.equals("world")) {
                // This handles when user is typing the world name with more characters
                return TabCompletionUtils.getWorldNames(sender, args[3]);
            }
            else if (target.equals("type")) {
                // Suggest entity types from the specified category
                return TabCompletionUtils.getEntityTypesFromCategory(args[2], args[3]);
            }
            else if (target.equals("entity")) {
                // Suggest specific entity types (could be used for future features)
                return TabCompletionUtils.getEntityTypes(args[3]);
            }
        }

        return Collections.emptyList();
    }

    /**
     * Handle debug command
     */
    private void handleDebugCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return;
        }

        Player player = (Player) sender;
        com.minekarta.realMobScale.managers.DebugManager debugManager = RealMobScale.getInstance().getDebugManager();

        if (args.length == 1) {
            // Toggle debug mode
            boolean newState = debugManager.toggleDebugMode(player);
            player.sendMessage("§aDebug mode " + (newState ? "§aenabled" : "§cdisabled") + " §ffor you!");

            if (newState) {
                player.sendMessage("§7You will now see debug messages when entities are scaled.");
                player.sendMessage("§7Use §f/realmobscale debug off §7to disable.");
            }
        } else {
            // Set debug mode explicitly
            String state = args[1].toLowerCase();
            boolean enable;

            switch (state) {
                case "on":
                case "true":
                case "enable":
                case "yes":
                    enable = true;
                    break;
                case "off":
                case "false":
                case "disable":
                case "no":
                    enable = false;
                    break;
                default:
                    player.sendMessage("§cUsage: /realmobscale debug <on|off>");
                    return;
            }

            boolean currentState = debugManager.isDebugMode(player);

            if (enable && !currentState) {
                debugManager.toggleDebugMode(player);
                player.sendMessage("§aDebug mode enabled!");
                player.sendMessage("§7You will now see debug messages when entities are scaled.");
            } else if (!enable && currentState) {
                debugManager.toggleDebugMode(player);
                player.sendMessage("§cDebug mode disabled!");
            } else {
                player.sendMessage("§7Debug mode is already " + (enable ? "enabled" : "disabled") + "!");
            }
        }
    }
}