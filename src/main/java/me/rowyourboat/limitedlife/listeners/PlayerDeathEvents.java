package me.rowyourboat.limitedlife.listeners;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerDeathEvents implements Listener {

    private final HashMap<UUID, Long> timeLostInSeconds = new HashMap<>();

    private final SaveHandler SaveHandler;
    private final JavaPlugin plugin;

    public PlayerDeathEvents() {
        this.SaveHandler = LimitedLife.SaveHandler;
        this.plugin = LimitedLife.plugin;
    }

    @EventHandler
    public void addTimeOnKill(PlayerDeathEvent event) {
        if (LimitedLife.currentGlobalTimerTask == null) return;
        Player deadPlayer = event.getEntity();

        FileConfiguration config = plugin.getConfig();

        List<String> boogeymenUUIDList = SaveHandler.getBoogeymenList();
        Player killer = deadPlayer.getKiller();
        if (killer != null) {
            if (boogeymenUUIDList.contains(killer.getUniqueId().toString())) {
                SaveHandler.cureBoogeyman(killer.getUniqueId().toString(), true);
                newPendingTimeSubtractionTitle(deadPlayer, config.getLong("boogeyman.time-lost-on-boogey-death"));
            } else {
                String killerTeamName = SaveHandler.convertTimeToTeamName(SaveHandler.getPlayerTimeLeft(killer));
                String victimTeamName = SaveHandler.convertTimeToTeamName(SaveHandler.getPlayerTimeLeft(deadPlayer));

                if (killerTeamName.equalsIgnoreCase("RED") && victimTeamName.equalsIgnoreCase("RED")){
                    SaveHandler.addPlayerTime(killer, LimitedLife.plugin.getConfig().getLong("rewards.time-gain-on-kill"));
                    newPendingTimeSubtractionTitle(deadPlayer, config.getLong("penalties.time-lost-on-death"));
                    return;
                }

                if (killerTeamName.equalsIgnoreCase(victimTeamName)) {

                    if (config.getBoolean("rewards.add-time-on-kill-same-colour"))
                        SaveHandler.addPlayerTime(killer, LimitedLife.plugin.getConfig().getLong("rewards.time-gain-on-kill"));

                    if (config.getBoolean("penalties.subtract-time-on-death-same-colour"))
                        newPendingTimeSubtractionTitle(deadPlayer, config.getLong("penalties.time-lost-on-death"));

                } else {
                    SaveHandler.addPlayerTime(killer, LimitedLife.plugin.getConfig().getLong("rewards.time-gain-on-kill"));
                    newPendingTimeSubtractionTitle(deadPlayer, config.getLong("penalties.time-lost-on-death"));
                }

            }
        } else {
            newPendingTimeSubtractionTitle(deadPlayer, config.getLong("penalties.time-lost-on-death"));
        }
    }

    public void newPendingTimeSubtractionTitle(Player deadPlayer, long timeToSubtract) {
        SaveHandler.subtractPlayerTime(deadPlayer, timeToSubtract, false);
        timeLostInSeconds.put(deadPlayer.getUniqueId(), timeToSubtract);
    }

    @EventHandler
    public void subtractTimeOnRespawn(PlayerRespawnEvent event) {
        if (LimitedLife.currentGlobalTimerTask == null) return;
        Player player = event.getPlayer();
        if (timeLostInSeconds.containsKey(player.getUniqueId())) {
            long timeToSubtract = timeLostInSeconds.get(player.getUniqueId());
            timeLostInSeconds.remove(player.getUniqueId());
            SaveHandler.sendTimeChangeTitle(player, ChatColor.RED + "-" + SecondsToClockFormat.convert(timeToSubtract, false));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline())
                    if (SaveHandler.getPlayerTimeLeft(player) == 0)
                        player.setGameMode(GameMode.SPECTATOR);
            }, 10);
        }
    }

}