package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.countdown.PlayerTimerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimerCommand {

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf timer <start|pause|reset> [player]");
        return true;
    }

    private static void sendTitleToPlayer(String title, Player player) {
        if (!player.isOnline()) return;
        player.sendTitle(title, null, 10, 40, 10);
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 1);
    }

    public static List<UUID> uuidTimerDisabledList = new ArrayList<>();

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1)
            return invalidSyntax(sender);

        if (args[1].equalsIgnoreCase("start")) {
            if (args.length == 2) {
                if (LimitedLife.globalTimerActive) {
                    sender.sendMessage(ChatColor.DARK_RED + "You've already started the global timer!");
                    return true;
                }
                LimitedLife.globalTimerActive = true;
                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    if (LimitedLife.SaveHandler.getPlayerTimeLeft(offlinePlayer) <= -1) // -1 is the default value
                        LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, LimitedLife.plugin.getConfig().getLong("start-time-in-hours")*(long)Math.pow(60,2));

                    BukkitScheduler scheduler = Bukkit.getScheduler();

                    Player player = offlinePlayer.getPlayer();
                    if (player != null) {
                        sendTitleToPlayer(ChatColor.GREEN + ChatColor.BOLD.toString() + "3", player);
                        scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayer(ChatColor.YELLOW + ChatColor.BOLD.toString() + "2", player), 40);
                        scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayer(ChatColor.RED + ChatColor.BOLD.toString() + "1", player), 80);
                        scheduler.runTaskLater(LimitedLife.plugin, () -> {
                            player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "The timer has begun!", null, 10, 40, 10);
                            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_2, 10, 1);
                        }, 120);
                    }

                    scheduler.runTaskLater(LimitedLife.plugin, () -> new PlayerTimerTask(offlinePlayer), 120);
                }
                sender.sendMessage(ChatColor.DARK_GREEN + "You've started/resumed the timer for everyone!");
                uuidTimerDisabledList.clear();
            } else {
                if (!LimitedLife.globalTimerActive) {
                    sender.sendMessage(ChatColor.DARK_RED + "You may only start the timer for individual players when the global timer is active!\n(/lf timer start)");
                    return true;
                }

                Player playerArg = Bukkit.getPlayer(args[2]);
                if (playerArg == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player doesn't exist!");
                    return true;
                }

                if (!uuidTimerDisabledList.contains(playerArg.getUniqueId())) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player already has an active timer!");
                    return true;
                }
                uuidTimerDisabledList.remove(playerArg.getUniqueId());

                new PlayerTimerTask(playerArg);
                sender.sendMessage(ChatColor.DARK_GREEN + "You've resumed " + playerArg.getName() + "'s timer!");
            }
        } else if (args[1].equalsIgnoreCase("pause")) {
            if (args.length == 2) {
                if (!LimitedLife.globalTimerActive) {
                    sender.sendMessage(ChatColor.DARK_RED + "The global timer isn't active!");
                    return true;
                }
                LimitedLife.globalTimerActive = false;
                sender.sendMessage(ChatColor.DARK_GREEN + "You've paused the timer for everyone!");
                uuidTimerDisabledList.clear();
            } else {
                if (!LimitedLife.globalTimerActive) {
                    sender.sendMessage(ChatColor.DARK_RED + "You may only pause the timer for individual players when the global timer is enabled!\n(/lf timer start)");
                    return true;
                }

                Player playerArg = Bukkit.getPlayer(args[2]);
                if (playerArg == null) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player doesn't exist!");
                    return true;
                }

                if (uuidTimerDisabledList.contains(playerArg.getUniqueId())) {
                    sender.sendMessage(ChatColor.DARK_RED + "That player's timer has already been paused!");
                    return true;
                }
                uuidTimerDisabledList.add(playerArg.getUniqueId());
                sender.sendMessage(ChatColor.DARK_GREEN + "You've paused " + playerArg.getName() + "'s timer!");
            }
        } else if (args[1].equalsIgnoreCase("reset")) {
            if (args.length < 3 || (args[2] == null || !args[2].equalsIgnoreCase("confirm"))) {
                sender.sendMessage(ChatColor.DARK_RED + "Are you sure? This will wipe all player data!\nRun '/lf timer reset confirm' to execute the command!");
                return true;
            }
            LimitedLife.globalTimerActive = false;
            uuidTimerDisabledList.clear();
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, -1);}
            sender.sendMessage(ChatColor.DARK_GREEN + "You've wiped all player data!");
        } else
            return invalidSyntax(sender);

        return true;
    }

}
