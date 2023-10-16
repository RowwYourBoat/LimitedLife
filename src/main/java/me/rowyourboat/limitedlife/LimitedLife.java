package me.rowyourboat.limitedlife;

import me.rowyourboat.limitedlife.commands.BoogeymanReminderCommand;
import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.commands.MainTabCompleter;
import me.rowyourboat.limitedlife.countdown.GlobalTimerTask;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.discord.RequestHandler;
import me.rowyourboat.limitedlife.listeners.*;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import me.rowyourboat.limitedlife.util.CustomRecipes;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class LimitedLife extends JavaPlugin {

    public static JavaPlugin plugin;

    public static SaveHandler SaveHandler;
    public static TeamHandler TeamHandler;
    public static CustomRecipes CustomRecipes;

    public static RequestHandler RequestHandler;

    public static GlobalTimerTask currentGlobalTimerTask;

    @Override
    public void onEnable() {
        plugin = this;
        plugin.saveDefaultConfig();

        // Initialize handlers
        RequestHandler = new RequestHandler();
        SaveHandler = new SaveHandler();
        TeamHandler = new TeamHandler();
        CustomRecipes = new CustomRecipes();

        // Register commands
        PluginCommand limitedlifeCommand = plugin.getCommand("limitedlife");
        if (limitedlifeCommand != null) {
            limitedlifeCommand.setExecutor(new MainCommandExecutor());
            limitedlifeCommand.setTabCompleter(new MainTabCompleter());
        }
        PluginCommand boogeyManReminderCommand = plugin.getCommand("amitheboogeyman");
        if (boogeyManReminderCommand != null)
            boogeyManReminderCommand.setExecutor(new BoogeymanReminderCommand());

        // Register events
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new EntityEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChatFormat(), plugin);
        Bukkit.getPluginManager().registerEvents(new EnchantmentLimitations(), plugin);
        Bukkit.getPluginManager().registerEvents(new RevivalItemEvents(), plugin);

        // Update reminders
        Logger logger = plugin.getLogger();
        if (getConfig().getBoolean("other.plugin-update-reminders")) {
            new UpdateChecker(plugin, 108589).getVersion(version -> {
                if (this.getDescription().getVersion().equals(version)) {
                    getLogger().info("The plugin is up to date!");
                } else {
                    logger.warning("There is a newer version available!  Running: " + this.getDescription().getVersion() + "  Newest: " + version);
                    logger.warning("You may download it here: https://www.spigotmc.org/resources/limited-life.108589/");
                }
            });
        }

        Configuration defaults = plugin.getConfig().getDefaults();
        if (defaults != null) {
            int configVersion = defaults.getInt("config-version");
            if (getConfig().getInt("config-version") != -1) {
                if (getConfig().getInt("config-version") != configVersion) {
                    logger.warning("Your configuration file is " + (configVersion - getConfig().getInt("config-version")) + " version(s) behind!");
                    logger.warning("To be able to access the newly added settings, please delete the current config.yml file and reload the plugin or server!");
                }
            }
        }

        // Indicate that the plugin has been loaded
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(ChatColor.GREEN + "[LimitedLife] The plugin has been loaded!");
        console.sendMessage(ChatColor.GREEN + "[LimitedLife] Run '/lf timer start' to start or resume the timer.");
        console.sendMessage(ChatColor.GREEN + "[LimitedLife] Run '/lf boogeyman roll' to roll the boogeyman.");
        console.sendMessage(ChatColor.GREEN + "[LimitedLife] Run '/lf help' for a list of all commands.");

        Bukkit.getOnlinePlayers().forEach(CustomRecipes::grant);
        initializeMetrics();

    }

    @Override
    public void onDisable() {
        SaveHandler.save();
        currentGlobalTimerTask = null;
    }

    public static void reloadPlugin(CommandSender sender) {
        Bukkit.resetRecipes();
        plugin.reloadConfig();
        HandlerList.unregisterAll();
        plugin.onDisable();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            sender.sendMessage(ChatColor.DARK_GREEN + "Success!");
            plugin.onEnable();
        }, 25);
    }

    private void initializeMetrics() {
        Metrics metrics = new Metrics(plugin, 17884);
        metrics.addCustomChart(new SimplePie("discord_integration", () -> String.valueOf(getConfig().getBoolean("discord-integration.enabled"))));
    }

}