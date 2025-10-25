package com.minekarta.realMobScale.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import com.minekarta.realMobScale.RealMobScale;
import com.minekarta.realMobScale.managers.PlayerPreferencesManager;
import com.minekarta.realMobScale.managers.PlayerPreferencesManager.PlayerPreferences;
import com.minekarta.realMobScale.managers.PlayerPreferencesManager.ScalingMode;
import com.minekarta.realMobScale.utils.TabCompletionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Command handler for player preferences
 * Allows players to customize their scaling experience
 */
public class PreferencesCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;
        PlayerPreferencesManager prefsManager = RealMobScale.getInstance().getPlayerPreferencesManager();
        PlayerPreferences prefs = prefsManager.getPlayerPreferences(player);

        if (args.length == 0) {
            showCurrentPreferences(player, prefs);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "toggle":
                toggleScaling(player, prefs);
                break;

            case "mode":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /prefs mode <global|category|disabled>");
                    return true;
                }
                setScalingMode(player, prefs, args[1]);
                break;

            case "multiplier":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /prefs multiplier <0.1-5.0>");
                    return true;
                }
                setMultiplier(player, prefs, args[1]);
                break;

            case "categories":
                if (args.length < 2) {
                    showCategories(player, prefs);
                } else {
                    toggleCategory(player, prefs, args[1]);
                }
                break;

            case "info":
                toggleInfoMode(player, prefs);
                break;

            case "reset":
                resetPreferences(player, prefs);
                break;

            default:
                showHelp(player);
                break;
        }

        return true;
    }

    private void showCurrentPreferences(Player player, PlayerPreferences prefs) {
        player.sendMessage("§6=== Your Scaling Preferences ===");
        player.sendMessage("§fScaling: " + (prefs.isEnabled() ? "§aEnabled" : "§cDisabled"));
        player.sendMessage("§fMode: §7" + prefs.getScalingMode().name());
        player.sendMessage("§fMultiplier: §7" + String.format("%.2f", prefs.getPersonalScaleMultiplier()) + "x");
        player.sendMessage("§fShow Info: " + (prefs.isShowScalingInfo() ? "§aYes" : "§cNo"));
        player.sendMessage("§fCategories: §7" + String.join(", ", prefs.getCategoriesEnabled()));
        player.sendMessage("");
        player.sendMessage("§7Use §f/prefs help §7for more options");
    }

    private void toggleScaling(Player player, PlayerPreferences prefs) {
        if (!player.hasPermission("realmobscale.user")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        boolean newState = !prefs.isEnabled();
        prefs.setEnabled(newState);
        RealMobScale.getInstance().getPlayerPreferencesManager().savePreferences();

        player.sendMessage("§aMob scaling " + (newState ? "§aenabled" : "§cdisabled") + " §ffor you!");
    }

    private void setScalingMode(Player player, PlayerPreferences prefs, String modeStr) {
        if (!player.hasPermission("realmobscale.user")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        try {
            ScalingMode mode = ScalingMode.valueOf(modeStr.toUpperCase());
            prefs.setScalingMode(mode);
            RealMobScale.getInstance().getPlayerPreferencesManager().savePreferences();

            player.sendMessage("§aScaling mode set to: §f" + mode.name());

            if (mode == ScalingMode.BY_CATEGORY) {
                player.sendMessage("§7Use §f/prefs categories §7to manage enabled categories");
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cInvalid mode! Use: global, category, or disabled");
        }
    }

    private void setMultiplier(Player player, PlayerPreferences prefs, String multiplierStr) {
        if (!player.hasPermission("realmobscale.user")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        try {
            double multiplier = Double.parseDouble(multiplierStr);
            if (multiplier < 0.1 || multiplier > 5.0) {
                player.sendMessage("§cMultiplier must be between 0.1 and 5.0!");
                return;
            }

            prefs.setPersonalScaleMultiplier(multiplier);
            RealMobScale.getInstance().getPlayerPreferencesManager().savePreferences();

            player.sendMessage("§aPersonal scale multiplier set to: §f" + String.format("%.2f", multiplier) + "x");
        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid number! Use a value between 0.1 and 5.0");
        }
    }

    private void showCategories(Player player, PlayerPreferences prefs) {
        player.sendMessage("§6=== Categories ===");
        for (String category : Arrays.asList("ANIMALS", "MONSTERS", "WATER_CREATURES", "FLYING_CREATURES", "BOSSES")) {
            boolean enabled = prefs.getCategoriesEnabled().contains(category);
            player.sendMessage("§f" + category + ": " + (enabled ? "§a✓" : "§c✗"));
        }
        player.sendMessage("");
        player.sendMessage("§7Use §f/prefs categories <category> §7to toggle");
    }

    private void toggleCategory(Player player, PlayerPreferences prefs, String category) {
        if (!player.hasPermission("realmobscale.user")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        category = category.toUpperCase();
        if (!Arrays.asList("ANIMALS", "MONSTERS", "WATER_CREATURES", "FLYING_CREATURES", "BOSSES").contains(category)) {
            player.sendMessage("§cInvalid category! Use: animals, monsters, water_creatures, flying_creatures, or bosses");
            return;
        }

        if (prefs.getCategoriesEnabled().contains(category)) {
            prefs.getCategoriesEnabled().remove(category);
            player.sendMessage("§cCategory " + category + " disabled");
        } else {
            prefs.getCategoriesEnabled().add(category);
            player.sendMessage("§aCategory " + category + " enabled");
        }

        // Make sure scaling mode is BY_CATEGORY
        if (prefs.getScalingMode() != ScalingMode.BY_CATEGORY) {
            prefs.setScalingMode(ScalingMode.BY_CATEGORY);
            player.sendMessage("§7Scaling mode automatically set to CATEGORY");
        }

        RealMobScale.getInstance().getPlayerPreferencesManager().savePreferences();
    }

    private void toggleInfoMode(Player player, PlayerPreferences prefs) {
        if (!player.hasPermission("realmobscale.user")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        boolean newState = !prefs.isShowScalingInfo();
        prefs.setShowScalingInfo(newState);
        RealMobScale.getInstance().getPlayerPreferencesManager().savePreferences();

        player.sendMessage("§aScaling info " + (newState ? "§aenabled" : "§cdisabled") + " §ffor you!");
    }

    private void resetPreferences(Player player, PlayerPreferences prefs) {
        if (!player.hasPermission("realmobscale.user")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return;
        }

        // Reset to defaults
        prefs.setEnabled(true);
        prefs.setScalingMode(ScalingMode.GLOBAL);
        prefs.setPersonalScaleMultiplier(1.0);
        prefs.setShowScalingInfo(false);
        prefs.setCategoriesEnabled(Arrays.asList("ANIMALS", "MONSTERS", "WATER_CREATURES", "FLYING_CREATURES", "BOSSES"));

        RealMobScale.getInstance().getPlayerPreferencesManager().savePreferences();

        player.sendMessage("§aYour preferences have been reset to defaults!");
    }

    private void showHelp(Player player) {
        player.sendMessage("§6=== Scaling Preferences Help ===");
        player.sendMessage("§f/prefs §7- Show current preferences");
        player.sendMessage("§f/prefs toggle §7- Enable/disable scaling");
        player.sendMessage("§f/prefs mode <mode> §7- Set scaling mode");
        player.sendMessage("§f/prefs multiplier <x> §7- Set personal multiplier (0.1-5.0)");
        player.sendMessage("§f/prefs categories §7- Show category status");
        player.sendMessage("§f/prefs categories <cat> §7- Toggle category");
        player.sendMessage("§f/prefs info §7- Toggle scaling info display");
        player.sendMessage("§f/prefs reset §7- Reset to defaults");
        player.sendMessage("");
        player.sendMessage("§7Modes: §fglobal, category, disabled");
        player.sendMessage("§7Categories: §fanimals, monsters, water_creatures, flying_creatures, bosses");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Only players can use preferences commands
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            // Main subcommands
            List<String> subCommands = Arrays.asList("toggle", "mode", "multiplier", "categories", "info", "reset", "show", "hide");
            return TabCompletionUtils.filterStartingWith(subCommands, args[0]);
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "mode":
                // Scaling modes
                if (args.length == 2) {
                    List<String> modes = Arrays.asList("global", "category", "disabled");
                    return TabCompletionUtils.filterStartingWith(modes, args[1]);
                }
                break;

            case "multiplier":
                // Common multiplier values
                if (args.length == 2) {
                    return TabCompletionUtils.filterStartingWith(TabCompletionUtils.COMMON_MULTIPLIERS, args[1]);
                }
                break;

            case "categories":
                // Available categories
                if (args.length == 2) {
                    List<String> categories = Arrays.asList("animals", "monsters", "water_creatures", "flying_creatures", "bosses");
                    return TabCompletionUtils.filterStartingWith(categories, args[1]);
                }
                else if (args.length == 3) {
                    // Suggest category names when typing the third argument
                    List<String> categories = Arrays.asList("animals", "monsters", "water_creatures", "flying_creatures", "bosses");
                    return TabCompletionUtils.filterStartingWith(categories, args[2]);
                }
                break;

            case "toggle":
                // Toggle options
                if (args.length == 2) {
                    List<String> toggleOptions = Arrays.asList("scaling", "info", "all");
                    return TabCompletionUtils.filterStartingWith(toggleOptions, args[1]);
                }
                break;

            case "show":
            case "hide":
                // Show/hide options
                if (args.length == 2) {
                    List<String> showOptions = Arrays.asList("scaling", "info", "stats", "debug");
                    return TabCompletionUtils.filterStartingWith(showOptions, args[1]);
                }
                break;

            case "reset":
                // Reset options
                if (args.length == 2) {
                    List<String> resetOptions = Arrays.asList("all", "multiplier", "categories", "mode", "info");
                    return TabCompletionUtils.filterStartingWith(resetOptions, args[1]);
                }
                break;

            default:
                break;
        }

        return Collections.emptyList();
    }
}