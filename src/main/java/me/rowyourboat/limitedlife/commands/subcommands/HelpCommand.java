package me.rowyourboat.limitedlife.commands.subcommands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class HelpCommand {

    private static HashMap<String, String> getHelpPages() {
        HashMap<String, String> helpPages = new HashMap<>();

        helpPages.put("boogeyman", ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + " \n \nBoogeyman Command Help" + ChatColor.RESET

            + ChatColor.GREEN + "\n\n/lf boogeyman roll [skiprolldelay]" + ChatColor.GRAY + " - Rolls the boogeyman after the specified amount of time (boogeyman.roll-delay-in-minutes), unless the 'skiprolldelay' value is specified. This will also clear the current Boogeymen list automatically." + ChatColor.RESET
            + ChatColor.GREEN + "\n\n/lf boogeyman cure <player>" + ChatColor.GRAY + " - Cures the specified player, even when offline. They will be notified (if online)." + ChatColor.RESET
            + ChatColor.GREEN + "\n\n/lf boogeyman punish" + ChatColor.GRAY + " - Punishes all existing Boogeymen for failing to secure a kill. This will lower them to the next colour." + ChatColor.RESET
            + ChatColor.GREEN + "\n\n/lf boogeyman clear" + ChatColor.GRAY + " - Cures all existing boogeymen. They will be notified."
            + " \n \n "

        );

        helpPages.put("modifytime", ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + " \n \nModifyTime Command Help" + ChatColor.RESET

                + ChatColor.GREEN + ChatColor.BOLD + "\n\n/lf modifytime <player> <+|-><num><h|m|s>" + ChatColor.GRAY +
                " - Removes/adds the specified amount of time to/from the player's timer." + ChatColor.RESET +
                ChatColor.GREEN + "\n\n<+|-> " + ChatColor.GRAY + "- Determines whether the specified amount of time will be added or removed. Must be '-' or '+'." +
                ChatColor.GREEN + "\n\n<num> " + ChatColor.GRAY + "- Amount of time to add/subtract." +
                ChatColor.GREEN + "\n\n<h|m|s> " + ChatColor.GRAY + "- Determines whether the time will be modified in hours, minutes or seconds." +
                ChatColor.GREEN + "\n\nExample " + ChatColor.GRAY + "- /lf modifytime <player> -3h (Subtracts 3 hours of time from the player's timer.)"
                + " \n \n "

        );

        helpPages.put("timer", ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + " \n \nTimer Command Help" + ChatColor.RESET

                + ChatColor.GREEN + "\n\n/lf timer start [player]" + ChatColor.GRAY + " - Starts/Resumes the timer for everyone, unless a player is specified, in which case it would only start for them." + ChatColor.RESET
                + ChatColor.GREEN + "\n\n/lf timer pause [player]" + ChatColor.GRAY + " - Pauses the timer for everyone, unless a player is specified, in which case it would only be paused for them." + ChatColor.RESET
                + ChatColor.GREEN + "\n\n/lf timer reset" + ChatColor.GRAY + " - Wipes all timer data. Will require a confirmation."
                + "\n\n "

        );

        return helpPages;
    }

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf help <boogeyman|modifytime|timer>");
        return true;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) return invalidSyntax(sender);
        if (!getHelpPages().containsKey(args[1].toLowerCase())) return invalidSyntax(sender);

        sender.sendMessage(getHelpPages().get(args[1].toLowerCase()));
        return true;
    }

}
