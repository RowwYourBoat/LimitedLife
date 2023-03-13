package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetTimeCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf gettime <player>");
        return true;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return invalidSyntax(sender);
        Player player = Bukkit.getPlayer(args[1]);
        if (player != null)
            sender.sendMessage(ChatColor.DARK_GREEN + player.getName() + " has " + SecondsToClockFormat.convert(LimitedLife.SaveHandler.getPlayerTimeLeft(player), false) + " left!");
        return true;
    }

}
