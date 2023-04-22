package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModifyTimeCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf modifytime <player> <+|-><num><h|m|s>\nExample: /lf modifytime " + sender.getName() + " +8h");
        return true;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3) return invalidSyntax(sender);
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ChatColor.DARK_RED + "That player doesn't exist!");
            return true;
        }
        String timeArgument = args[2];
        if (timeArgument == null) return invalidSyntax(sender);

        boolean subtract;
        if (timeArgument.startsWith("+"))
            subtract = false;
        else if (timeArgument.startsWith("-"))
            subtract = true;
        else return invalidSyntax(sender);

        long timeAmountToModify;
        try {
            timeAmountToModify = Long.parseLong(timeArgument.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return invalidSyntax(sender);
        }

        String timeType;
        if (timeArgument.endsWith("h"))
            timeType = "h";
        else if (timeArgument.endsWith("m"))
            timeType = "m";
        else if (timeArgument.endsWith("s"))
            timeType = "s";
        else return invalidSyntax(sender);

        if (subtract) {
            if (timeType.equalsIgnoreCase("h")) {
                LimitedLife.SaveHandler.subtractPlayerTime(player, timeAmountToModify * 60 * 60);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've subtracted " + timeAmountToModify + " hours from " + player.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Subtracted " + timeAmountToModify + " hours from " + player.getName() + "'s timer");
            } else if (timeType.equalsIgnoreCase("m")) {
                LimitedLife.SaveHandler.subtractPlayerTime(player, timeAmountToModify * 60);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've subtracted " + timeAmountToModify + " minutes from " + player.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Subtracted " + timeAmountToModify + " minutes from " + player.getName() + "'s timer");
            } else {
                LimitedLife.SaveHandler.subtractPlayerTime(player, timeAmountToModify);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've subtracted " + timeAmountToModify + " seconds from " + player.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Subtracted " + timeAmountToModify + " seconds from " + player.getName() + "'s timer");
            }
        } else {
            if (timeType.equalsIgnoreCase("h")) {
                LimitedLife.SaveHandler.addPlayerTime(player, timeAmountToModify * 60 * 60);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've added " + timeAmountToModify + " hours to " + player.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Added " + timeAmountToModify + " hours to " + player.getName() + "'s timer");
            } else if (timeType.equalsIgnoreCase("m")) {
                LimitedLife.SaveHandler.addPlayerTime(player, timeAmountToModify * 60);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've added " + timeAmountToModify + " minutes to " + player.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Added " + timeAmountToModify + " minutes to " + player.getName() + "'s timer");
            } else {
                LimitedLife.SaveHandler.addPlayerTime(player, timeAmountToModify);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've added " + timeAmountToModify + " minutes to " + player.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Added " + timeAmountToModify + " minutes to " + player.getName() + "'s timer");
            }
            if (LimitedLife.SaveHandler.getPlayerTimeLeft(player) > 0)
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
        }
        return true;
    }

}
