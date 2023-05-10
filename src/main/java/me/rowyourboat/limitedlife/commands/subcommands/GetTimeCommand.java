package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class GetTimeCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf gettime <player>");
        return true;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return invalidSyntax(sender);
        OfflinePlayer offlinePlayer = LimitedLife.SaveHandler.getPlayerByName(args[1]);
        if (offlinePlayer != null)
            sender.sendMessage(ChatColor.DARK_GREEN + offlinePlayer.getName() + " has " + SecondsToClockFormat.convert(LimitedLife.SaveHandler.getPlayerTimeLeft(offlinePlayer), false) + " left!");
        return true;
    }

}
