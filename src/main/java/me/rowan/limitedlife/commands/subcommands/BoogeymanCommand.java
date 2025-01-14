package me.rowan.limitedlife.commands.subcommands;

import me.rowan.limitedlife.LimitedLife;
import me.rowan.limitedlife.commands.MainCommandExecutor;
import me.rowan.limitedlife.data.SaveHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BoogeymanCommand {

    private static boolean rolling = false;
    private final static ArrayList<Integer> taskIds = new ArrayList<>();

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf boogeyman <roll|cure|punish|clear> [player]");
        return true;
    }

    private static void sendTitleToPlayers(String title, Sound sound) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, null, 10, 40, 10);
            player.playSound(player, sound, 1, 1);
        }
    }

    private static void rollBoogeyman(int requestedAmount) {
        List<UUID> boogeymenList = new ArrayList<>();
        List<UUID> playerUUIDs = new ArrayList<>();

        boolean redBoogeymenAllowed = LimitedLife.plugin.getConfig().getBoolean("boogeyman.red-boogeymen");
        long redColourThreshold = LimitedLife.plugin.getConfig().getLong("name-colour-thresholds.red-name");

        int candidateAmount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            long playerTimeLeft = LimitedLife.SaveHandler.getPlayerTimeLeft(player);
            if (redBoogeymenAllowed) {
                if (playerTimeLeft > 0)
                    candidateAmount++;
            } else if (playerTimeLeft > redColourThreshold)
                candidateAmount++;
        }
        if (candidateAmount == 0) {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "There aren't enough players left for there to be a boogeyman!");
            rolling = false;
            return;
        }
        if (candidateAmount < requestedAmount)
            requestedAmount = candidateAmount;

        for (Player player : Bukkit.getOnlinePlayers()) {
            long playerTimeLeft = LimitedLife.SaveHandler.getPlayerTimeLeft(player);
            if (redBoogeymenAllowed && playerTimeLeft != 0)
                playerUUIDs.add(player.getUniqueId());
            else if (playerTimeLeft > redColourThreshold)
                playerUUIDs.add(player.getUniqueId());
        }

        for (int i = requestedAmount ; i > 0 ; i--) {
            UUID chosenPlayer = null;
            while (chosenPlayer == null) {
                Random random = new Random();
                int r = random.nextInt(playerUUIDs.size());
                if (!boogeymenList.contains(playerUUIDs.get(r))) {
                    chosenPlayer = playerUUIDs.get(r);
                    boogeymenList.add(chosenPlayer);
                }
            }
        }
        LimitedLife.SaveHandler.setBoogeymen(boogeymenList);

        for (UUID playerUUID : playerUUIDs) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                if (boogeymenList.contains(playerUUID)) {
                    player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman", null, 10, 60, 10);
                    if (LimitedLife.plugin.getConfig().getBoolean("sound-effects.enabled"))
                        player.playSound(player, Sound.valueOf(LimitedLife.plugin.getConfig().getString("sound-effects.boogeyman-chosen")), 1, 1);
                } else {
                    player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "NOT The Boogeyman", null, 10, 60, 10);
                    if (LimitedLife.plugin.getConfig().getBoolean("sound-effects.enabled"))
                        player.playSound(player, Sound.valueOf(LimitedLife.plugin.getConfig().getString("sound-effects.boogeyman-not-chosen")), 1, 1);
                }
            }
        }

        rolling = false;
    }

    private static void queueAndSendTitles() {
        FileConfiguration config = LimitedLife.plugin.getConfig();
        int max = config.getInt("boogeyman.amount-max") + 1;
        int min = config.getInt("boogeyman.amount-min");

        Sound countdownSound;
        try {
            countdownSound = Sound.valueOf(LimitedLife.plugin.getConfig().getString("sound-effects.countdown"));
        } catch (IllegalArgumentException e) {
            countdownSound = null;
        }
        final Sound finalCountdownSound = countdownSound;
        BukkitScheduler scheduler = Bukkit.getScheduler();
        Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman is now being chosen!");
        sendTitleToPlayers(ChatColor.GREEN + ChatColor.BOLD.toString() + "3", countdownSound);
        scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.YELLOW + ChatColor.BOLD.toString() + "2", finalCountdownSound), 50);
        scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.RED + ChatColor.BOLD.toString() + "1", finalCountdownSound), 100);
        scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.YELLOW + ChatColor.BOLD.toString() + "You are..", null), 150);
        scheduler.runTaskLater(LimitedLife.plugin, () -> {
            if (max == min)
                rollBoogeyman(max);
            else {
                Random r = new Random();
                int amount = (r.nextInt(max - min) + min);
                rollBoogeyman(amount);
            }
        }, 200);
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1) return invalidSyntax(sender);
        SaveHandler SaveHandler = LimitedLife.SaveHandler;

        if (args[1].equalsIgnoreCase("clear")) {
            SaveHandler.cureAllBoogeymen();
            sender.sendMessage(ChatColor.DARK_GREEN + "You've cured all existing boogeymen!");
            MainCommandExecutor.commandFeedback(sender, "Cured all existing boogeymen");
        } else if (args[1].equalsIgnoreCase("cancel") && rolling) {
            taskIds.forEach(id -> Bukkit.getScheduler().cancelTask(id));
            rolling = false;
            sender.sendMessage(ChatColor.DARK_GREEN + "You've cancelled the boogeyman roll!");
        } else if (args[1].equalsIgnoreCase("punish")) {
            SaveHandler.punishBoogeymen();
            sender.sendMessage(ChatColor.DARK_GREEN + "The existing boogeymen have all been punished for not securing a kill!");
            MainCommandExecutor.commandFeedback(sender, "Punished all remaining boogeymen");
        } else if (args[1].equalsIgnoreCase("cure")) {
            if (args.length < 3)
                return invalidSyntax(sender);

            String playerName = args[2];
            OfflinePlayer finalOfflinePlayer = null;
            for (String boogeyUUIDString : SaveHandler.getBoogeymenList()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(boogeyUUIDString));
                if (offlinePlayer.getName() != null) {
                    if (offlinePlayer.getName().equalsIgnoreCase(playerName)) {
                        finalOfflinePlayer = offlinePlayer;
                        break;
                    }
                }
            }
            if (finalOfflinePlayer == null) {
                sender.sendMessage(ChatColor.DARK_RED + "That player either doesn't exist, or isn't a boogeyman!");
                return true;
            }

            SaveHandler.cureBoogeyman(finalOfflinePlayer.getUniqueId().toString(), true);
            sender.sendMessage(ChatColor.DARK_GREEN + "You've successfully cured " + finalOfflinePlayer.getName() + "!");
            MainCommandExecutor.commandFeedback(sender, "Cured " + finalOfflinePlayer.getName());
        } else if (args[1].equalsIgnoreCase("roll")) {

            if (LimitedLife.currentGlobalTimerTask == null) {
                sender.sendMessage(ChatColor.DARK_RED + "The global timer must be active to roll the boogeyman!");
                return true;
            }

            if (rolling) return true;
            rolling = true;
            MainCommandExecutor.commandFeedback(sender, "Rolled the boogeyman");

            FileConfiguration config = LimitedLife.plugin.getConfig();

            final long rollDelayInMinutes;
            if (args.length == 3 && args[2].equalsIgnoreCase("skiprolldelay"))
                rollDelayInMinutes = 0;
            else
                rollDelayInMinutes = LimitedLife.plugin.getConfig().getLong("boogeyman.roll-delay-in-minutes");


            if (rollDelayInMinutes != 0) {

                Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman will be chosen in " + rollDelayInMinutes + " minutes!");
                BukkitScheduler scheduler = Bukkit.getScheduler();
                if (config.getBoolean("boogeyman.reminders")) {
                    taskIds.add(scheduler.runTaskLater(LimitedLife.plugin, () -> Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman will be chosen in " + rollDelayInMinutes / 2 + " minutes!"), (rollDelayInMinutes / 2) * 60 * 20).getTaskId());
                    taskIds.add(scheduler.runTaskLater(LimitedLife.plugin, () -> Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman is about to be chosen!"), (long) (rollDelayInMinutes * .90) * 60 * 20).getTaskId());
                }
                taskIds.add(scheduler.runTaskLater(LimitedLife.plugin, BoogeymanCommand::queueAndSendTitles, rollDelayInMinutes * 60 * 20).getTaskId());

            } else
                queueAndSendTitles();

        }
        return true;
    }

}
