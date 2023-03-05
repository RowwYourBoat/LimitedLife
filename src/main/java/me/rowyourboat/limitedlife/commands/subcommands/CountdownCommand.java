package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.countdown.PlayerCountdownTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CountdownCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf countdown <start|pause|reset> [player]");
        return true;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1)
            return invalidSyntax(sender);

        if (args[1].equalsIgnoreCase("start")) {
            if (args[2] == null) {
                for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                    if (LimitedLife.SaveHandler.getPlayerTimeLeft(player) == -1) // -1 is the default value
                        LimitedLife.SaveHandler.setPlayerTimeLeft(player, LimitedLife.plugin.getConfig().getLong("start-time-in-hours")*(long)Math.pow(60,2));

                    LimitedLife.countdownActive = true;
                    new PlayerCountdownTask(player);
                }
            }
        } else if (args[1].equalsIgnoreCase("pause")) {
            LimitedLife.countdownActive = false;
        } else if (args[1].equalsIgnoreCase("reset")) {

        } else
            return invalidSyntax(sender);

        return true;
    }

}
