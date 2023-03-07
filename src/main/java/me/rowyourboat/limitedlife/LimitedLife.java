package me.rowyourboat.limitedlife;

import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.commands.MainTabCompleter;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.listeners.ChatFormat;
import me.rowyourboat.limitedlife.listeners.InventoryEvents;
import me.rowyourboat.limitedlife.listeners.PlayerDeathEvents;
import me.rowyourboat.limitedlife.listeners.PlayerJoinEvents;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import me.rowyourboat.limitedlife.util.CustomRecipes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LimitedLife extends JavaPlugin {

    public static JavaPlugin plugin;

    public static SaveHandler SaveHandler;
    public static TeamHandler TeamHandler;
    public static CustomRecipes CustomRecipes;

    public static boolean globalTimerActive;

    @Override
    public void onEnable() {
        plugin = this;
        globalTimerActive = false;

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

        plugin.saveDefaultConfig();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[LimitedLife] The plugin has been loaded!"
                + "\nPlease run the command '/lf timer start' to get started, or to resume everyone's timer!"
                + "\nRun '/lf boogeyman roll' to roll the boogeyman!"
                + "\nRun '/lf help' for a list of all commands!"
        );

        Bukkit.getOnlinePlayers().forEach(CustomRecipes::grant);
    }

    @Override
    public void onDisable() {
        globalTimerActive = false;
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
