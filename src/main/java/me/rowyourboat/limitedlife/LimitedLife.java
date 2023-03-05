package me.rowyourboat.limitedlife;

import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.commands.MainTabCompleter;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class LimitedLife extends JavaPlugin {

    public static JavaPlugin plugin;

    public static SaveHandler SaveHandler;
    public static TeamHandler TeamHandler;

    public static boolean countdownActive;

    @Override
    public void onEnable() {
        plugin = this;
        countdownActive = false;

        SaveHandler = new SaveHandler();
        TeamHandler = new TeamHandler();

        plugin.getCommand("limitedlife").setExecutor(new MainCommandExecutor());
        plugin.getCommand("limitedlife").setTabCompleter(new MainTabCompleter());

        plugin.saveDefaultConfig();
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[LimitedLife] The plugin has been loaded!"
                + "\nPlease run the command '/lf countdown start' to get started, or to resume everyone's countdown timer!"
                + "\nRun '/lf boogeyman roll' to roll the boogeyman!"
        );
    }

    @Override
    public void onDisable() {
        countdownActive = false;
    }

}
