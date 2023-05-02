package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import me.rowyourboat.limitedlife.countdown.GlobalTimerTask;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class TimerCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf timer <start|pause|reset> [player]");
        return true;
    }

    private static void sendTitleToPlayers(String title, Sound sound) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, null, 10, 40, 10);
            player.playSound(player, sound, 1, 1);
        }
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1)
            return invalidSyntax(sender);

        if (args[1].equalsIgnoreCase("start")) {
            if (args.length == 2) {
                if (LimitedLife.currentGlobalTimerTask != null) {
                    sender.sendMessage(ChatColor.DARK_RED + "You've already started the global timer!");
                    return true;
                }

                BukkitScheduler scheduler = Bukkit.getScheduler();
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (LimitedLife.SaveHandler.getPlayerTimeLeft(offlinePlayer) <= -1) // -1 is the default value
                        LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, LimitedLife.plugin.getConfig().getLong("timer.start-time-in-seconds"));

                    scheduler.runTaskLater(LimitedLife.plugin, () -> {
                        if (LimitedLife.currentGlobalTimerTask == null)
                            LimitedLife.currentGlobalTimerTask = new GlobalTimerTask();

                        if (!LimitedLife.currentGlobalTimerTask.playerHasActiveTimer(offlinePlayer)) {
                            LimitedLife.currentGlobalTimerTask.startPlayerTimer(offlinePlayer);
                        }
                    }, 120);
                }

                sendTitleToPlayers(ChatColor.GREEN + ChatColor.BOLD.toString() + "3", Sound.BLOCK_NOTE_BLOCK_CHIME);
                scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.YELLOW + ChatColor.BOLD.toString() + "2", Sound.BLOCK_NOTE_BLOCK_CHIME), 40);
                scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.RED + ChatColor.BOLD.toString() + "1", Sound.BLOCK_NOTE_BLOCK_CHIME), 80);
                scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.GREEN + ChatColor.BOLD.toString() + "The timer has begun!", Sound.ITEM_GOAT_HORN_SOUND_2), 120);

                sender.sendMessage(ChatColor.DARK_GREEN + "You've started/resumed the timer for everyone!");
                MainCommandExecutor.commandFeedback(sender, "Resumed the timer for everyone");
            } else {
                if (LimitedLife.currentGlobalTimerTask == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "You may only start the timer for individual players when the global timer is active!\n(/lf timer start)");
                    return true;
                }

                Player playerArg = Bukkit.getPlayer(args[2]);
                if (playerArg == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player doesn't exist!");
                    return true;
                }

                if (LimitedLife.currentGlobalTimerTask.playerHasActiveTimer(playerArg)) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player already has an active timer!");
                    return true;
                }
                LimitedLife.currentGlobalTimerTask.startPlayerTimer(playerArg);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've resumed " + playerArg.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Resumed " + playerArg.getName() + "'s timer!");
            }
        } else if (args[1].equalsIgnoreCase("pause")) {
            if (args.length == 2) {
                if (LimitedLife.currentGlobalTimerTask == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "The global timer isn't active!");
                    return true;
                }
                LimitedLife.currentGlobalTimerTask = null;
                sender.sendMessage(ChatColor.DARK_GREEN + "You've paused the timer for everyone!");
                MainCommandExecutor.commandFeedback(sender, "Paused the timer for everyone");
            } else {
                if (LimitedLife.currentGlobalTimerTask == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "You may only pause the timer for individual players when the global timer is enabled!\n(/lf timer start)");
                    return true;
                }

                Player playerArg = Bukkit.getPlayer(args[2]);
                if (playerArg == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player doesn't exist!");
                    return true;
                }

                if (!LimitedLife.currentGlobalTimerTask.playerHasActiveTimer(playerArg)) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player's timer has already been paused!");
                    return true;
                }
                LimitedLife.currentGlobalTimerTask.pausePlayerTimer(playerArg);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've paused " + playerArg.getName() + "'s timer!");
                MainCommandExecutor.commandFeedback(sender, "Paused " + playerArg.getName() + "'s timer");
            }
        } else if (args[1].equalsIgnoreCase("reset")) {
            if (args.length < 3 || (args[2] == null || !args[2].equalsIgnoreCase("confirm"))) {
                sender.sendMessage(ChatColor.DARK_RED + "Are you sure? This will wipe all player data!\nRun '/lf timer reset confirm' to execute the command!");
                return true;
            }
            LimitedLife.currentGlobalTimerTask = null;
            Bukkit.getScheduler().runTaskLater(LimitedLife.plugin, () -> {
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, -1);}
                LimitedLife.SaveHandler.getMarkedAsDeadList().clear();
            }, 25);
            LimitedLife.TeamHandler.clearTeamMembers();
            sender.sendMessage(ChatColor.DARK_GREEN + "You've wiped all player data!");
            MainCommandExecutor.commandFeedback(sender, "Wiped all player data");
        } else
            return invalidSyntax(sender);

        return true;
    }

}
