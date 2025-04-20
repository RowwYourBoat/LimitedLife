package me.rowan.limitedlife.commands.subcommands;

import me.rowan.limitedlife.LimitedLife;
import me.rowan.limitedlife.commands.MainCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ModifyTimeCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf modifytime <player> <+|-><num><h|m|s>\nExample: /lf modifytime " + sender.getName() + " +8h");
        return true;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3) return invalidSyntax(sender);
        List<Player> players = MainCommandExecutor.getPlayersFromArgument(sender, args[1]);
        if (players == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Unable to find player(s).");
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

        HashMap<String, Double> powerOfHash = new HashMap<>();
        powerOfHash.put("h", 2D);
        powerOfHash.put("m", 1D);
        powerOfHash.put("s", 0D);
        double product = Math.pow(60, powerOfHash.get(timeType));

        if (subtract) {
            players.forEach(player -> LimitedLife.SaveHandler.subtractPlayerTime(player, Double.valueOf(timeAmountToModify * product).longValue(), true));
            String name = "everyone";
            if (players.size() == 1) name = players.get(0).getName();
            sender.sendMessage(ChatColor.DARK_GREEN + "You've subtracted " + timeAmountToModify + timeType + " from " + name + "'s timer!");
            MainCommandExecutor.commandFeedback(sender, "Subtracted " + timeAmountToModify + timeType + " from " + name + "'s timer");
        } else {
            players.forEach(player -> {
                LimitedLife.SaveHandler.addPlayerTime(player, Double.valueOf(timeAmountToModify * product).longValue());
                if (LimitedLife.SaveHandler.getPlayerTimeLeft(player) > 0)
                    LimitedLife.SaveHandler.removePlayerDeathMark(player);
            });
            String name = "everyone";
            if (players.size() == 1) name = players.get(0).getName();
            sender.sendMessage(ChatColor.DARK_GREEN + "You've added " + timeAmountToModify + timeType + " to " + name + "'s timer!");
            MainCommandExecutor.commandFeedback(sender, "Added " + timeAmountToModify + timeType + " to " + name + "'s timer");
        }
        return true;
    }

}
