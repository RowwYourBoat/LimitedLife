package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.data.SaveHandler;
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

    private static boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_RED + "Invalid Syntax!\n/lf boogeyman <roll|cure|punish|clear> [player]");
        return true;
    }

    private static void sendTitleToPlayers(String title, Sound sound) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, null, 10, 40, 10);
            player.playSound(player, sound, 3, 1);
        }
    }

    private static void rollBoogeyman(int amount) {
        List<UUID> boogeymenList = new ArrayList<>();
        List<UUID> playerUUIDs = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (LimitedLife.plugin.getConfig().getBoolean("boogeyman.red-boogeymen"))
                playerUUIDs.add(player.getUniqueId());
            else if (LimitedLife.SaveHandler.getPlayerTimeLeft(player) > LimitedLife.plugin.getConfig().getLong("name-colour-thresholds.red-name"))
                playerUUIDs.add(player.getUniqueId());
        }
        if (playerUUIDs.size() < amount) {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "Not enough green/yellow names!");
            rolling = false;
            return;
        }
        for (int i = amount ; i > 0 ; i--) {
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
                    player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 3, 1);
                } else {
                    player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "NOT The Boogeyman", null, 10, 60, 10);
                    player.playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 3, 1);
                }
            }
        }

        rolling = false;
    }

    public static boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1) return invalidSyntax(sender);
        SaveHandler SaveHandler = LimitedLife.SaveHandler;

        if (args[1].equalsIgnoreCase("clear")) {
            SaveHandler.getBoogeymenList().forEach(SaveHandler::cureBoogeyman);
            sender.sendMessage(ChatColor.DARK_GREEN + "You've cured all existing boogeymen!");
        } else if (args[1].equalsIgnoreCase("punish")) {
            SaveHandler.punishBoogeymen();
            sender.sendMessage(ChatColor.DARK_GREEN + "The existing boogeymen have all been punished for not securing a kill!");
        } else if (args[1].equalsIgnoreCase("roll")) {
            if (!LimitedLife.globalTimerActive) {
                sender.sendMessage(ChatColor.DARK_RED + "The global timer must be active to roll the boogeyman!");
                return true;
            }
            if (rolling) return true;
            rolling = true;
            FileConfiguration config = LimitedLife.plugin.getConfig();
            int max = config.getInt("boogeyman.amount-max")+1;
            int min = config.getInt("boogeyman.amount-min");

            long rollDelayInMinutes = LimitedLife.plugin.getConfig().getLong("boogeyman.roll-delay-in-minutes");

            Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman will be chosen in " + rollDelayInMinutes + " minutes!");
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(LimitedLife.plugin, () -> Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman will be chosen in " + rollDelayInMinutes/2 + " minutes!"), (rollDelayInMinutes/2)*60*20);
            scheduler.runTaskLater(LimitedLife.plugin, () -> Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman is about to be chosen!"),(long) (rollDelayInMinutes*.90)*60*20);
            scheduler.runTaskLater(LimitedLife.plugin, () -> {
                Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "The Boogeyman is now being chosen!");
                sendTitleToPlayers(ChatColor.GREEN + ChatColor.BOLD.toString() + "3", Sound.BLOCK_NOTE_BLOCK_CHIME);
                scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.YELLOW + ChatColor.BOLD.toString() + "2", Sound.BLOCK_NOTE_BLOCK_CHIME), 50);
                scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.RED + ChatColor.BOLD.toString() + "1", Sound.BLOCK_NOTE_BLOCK_CHIME), 100);
                scheduler.runTaskLater(LimitedLife.plugin, () -> sendTitleToPlayers(ChatColor.YELLOW + ChatColor.BOLD.toString() + "You are..", Sound.BLOCK_NOTE_BLOCK_BANJO), 150);
                scheduler.runTaskLater(LimitedLife.plugin, () -> {
                    if (max == min)
                        rollBoogeyman(max);
                    else {
                        Random r = new Random();
                        int amount = (r.nextInt(max - min) + min);
                        rollBoogeyman(amount);
                    }
                }, 200);
            }, rollDelayInMinutes*60*20);
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

            SaveHandler.cureBoogeyman(finalOfflinePlayer.getUniqueId().toString());
            sender.sendMessage(ChatColor.DARK_GREEN + "You've successfully cured " + finalOfflinePlayer.getName() + "!");
        }

        return true;
    }

}
