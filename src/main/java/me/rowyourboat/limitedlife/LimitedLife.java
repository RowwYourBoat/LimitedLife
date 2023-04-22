package me.rowyourboat.limitedlife;

import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.commands.MainTabCompleter;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.listeners.*;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import me.rowyourboat.limitedlife.util.CustomRecipes;
import me.rowyourboat.limitedlife.util.bStatsCode;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class LimitedLife extends JavaPlugin {

    public static JavaPlugin plugin;

    public static SaveHandler SaveHandler;
    public static TeamHandler TeamHandler;
    public static CustomRecipes CustomRecipes;

    public static boolean globalTimerActive;
    public static ArrayList<UUID> playersActiveTimerList;

    @Override
    public void onEnable() {
        int configVersion = 2;
        plugin = this;
        globalTimerActive = false;

        playersActiveTimerList = new ArrayList<>();

        SaveHandler = new SaveHandler();
        TeamHandler = new TeamHandler();
        CustomRecipes = new CustomRecipes();

        PluginCommand limitedlifeCommand = plugin.getCommand("limitedlife");
        if (limitedlifeCommand != null) {
            limitedlifeCommand.setExecutor(new MainCommandExecutor());
            limitedlifeCommand.setTabCompleter(new MainTabCompleter());
        }

        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new InventoryEvents(), plugin);
        Bukkit.getPluginManager().registerEvents(new ChatFormat(), plugin);
        Bukkit.getPluginManager().registerEvents(new EnchantmentLimitations(), plugin);

        plugin.saveDefaultConfig();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[LimitedLife] The plugin has been loaded!"
                + "\nPlease run the command '/lf timer start' to get started, or to resume everyone's timer!"
                + "\nRun '/lf boogeyman roll' to roll the boogeyman!"
                + "\nRun '/lf help' for a list of all commands!"
        );

        Bukkit.getOnlinePlayers().forEach(CustomRecipes::grant);

        new Metrics(plugin, bStatsCode.get());

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
        globalTimerActive = false;
        playersActiveTimerList.clear();
        SaveHandler.save();
    }

    public static void reloadPlugin(CommandSender sender) {
        Bukkit.resetRecipes();
        plugin.reloadConfig();
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
