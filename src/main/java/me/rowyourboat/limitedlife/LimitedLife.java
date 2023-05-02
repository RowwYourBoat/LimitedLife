package me.rowyourboat.limitedlife;

import me.rowyourboat.limitedlife.commands.BoogeymanReminderCommand;
import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.commands.MainTabCompleter;
import me.rowyourboat.limitedlife.countdown.GlobalTimerTask;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.listeners.*;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import me.rowyourboat.limitedlife.util.CustomRecipes;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class LimitedLife extends JavaPlugin {

    public static JavaPlugin plugin;

    public static SaveHandler SaveHandler;
    public static TeamHandler TeamHandler;
    public static CustomRecipes CustomRecipes;

    public static GlobalTimerTask currentGlobalTimerTask;

    @Override
    public void onEnable() {
        int configVersion = 4;
        plugin = this;

        SaveHandler = new SaveHandler();
        TeamHandler = new TeamHandler();
        CustomRecipes = new CustomRecipes();

        PluginCommand limitedlifeCommand = plugin.getCommand("limitedlife");
        if (limitedlifeCommand != null) {
            limitedlifeCommand.setExecutor(new MainCommandExecutor());
            limitedlifeCommand.setTabCompleter(new MainTabCompleter());
        }
        PluginCommand boogeyManReminderCommand = plugin.getCommand("amitheboogeyman");
        if (boogeyManReminderCommand != null)
            boogeyManReminderCommand.setExecutor(new BoogeymanReminderCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChatFormat(), plugin);
        Bukkit.getPluginManager().registerEvents(new EnchantmentLimitations(), plugin);
        Bukkit.getPluginManager().registerEvents(new RevivalItemEvents(), plugin);

        plugin.saveDefaultConfig();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "[LimitedLife] The plugin has been loaded!"
                + ChatColor.RESET + ChatColor.GREEN + "\nPlease run the command '/lf timer start' to get started, or to resume everyone's timer!"
                + "\nRun '/lf boogeyman roll' to roll the boogeyman!"
                + "\nRun '/lf help' for a list of all commands!"
        );

        Bukkit.getOnlinePlayers().forEach(CustomRecipes::grant);

        new Metrics(plugin, 17884);

        if (getConfig().getBoolean("other.plugin-update-reminders"))
            new UpdateChecker(plugin, 108589).getVersion(version -> {
                if (this.getDescription().getVersion().equals(version)) {
                    getLogger().info("The plugin is up to date!");
                } else {
                    getLogger().warning("There is a newer version available!  Running: " + this.getDescription().getVersion() + "  Newest: " + version);
                    getLogger().warning("You may download it here: https://www.spigotmc.org/resources/limited-life.108589/");
                }
            });

        if (getConfig().getInt("config-version") != -1)
            if (getConfig().getInt("config-version") != configVersion) {
                getLogger().warning("Your configuration file is " + (configVersion - getConfig().getInt("config-version")) + " version(s) behind!");
                getLogger().warning("To be able to access the newly added settings, please delete the current config.yml file and reload the plugin or server!");
            }
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

    // Sound.ENTITY_ENDER_DRAGON_DEATH = Chosen as Boogey and Failed Sound
    // BLOCK_NOTE_BLOCK_DIDGERIDO = Not chosen as boogey and Cured Sound
    // Sound.BLOCK_NOTE_BLOCK_CHIME = Countdown Sound

}
