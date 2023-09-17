package me.rowyourboat.limitedlife.countdown;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.data.SaveHandler;
import me.rowyourboat.limitedlife.scoreboard.TeamHandler;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GlobalTimerTask {

    private final JavaPlugin plugin;
    private final SaveHandler SaveHandler;
    private final TeamHandler TeamHandler;

    private final ArrayList<UUID> activeTimerList;

    public void pausePlayerTimer(OfflinePlayer offlinePlayer) { activeTimerList.remove(offlinePlayer.getUniqueId()); }

    public void startPlayerTimer(OfflinePlayer offlinePlayer) { activeTimerList.add(offlinePlayer.getUniqueId()); }

    public boolean playerHasActiveTimer(OfflinePlayer offlinePlayer) { return activeTimerList.contains(offlinePlayer.getUniqueId()); }

    public GlobalTimerTask() {
        this.plugin = LimitedLife.plugin;
        this.SaveHandler = LimitedLife.SaveHandler;
        this.TeamHandler = LimitedLife.TeamHandler;

        this.activeTimerList = new ArrayList<>();

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (LimitedLife.currentGlobalTimerTask == null) {this.cancel(); return;}
                LimitedLife.SaveHandler.countDownPluginSecond();

                for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                    long playerTimeLeft = SaveHandler.getPlayerTimeLeft(offlinePlayer);
                    if (activeTimerList.contains(offlinePlayer.getUniqueId())) {

                        if (offlinePlayer.isOnline() || !plugin.getConfig().getBoolean("timer.ignore-offline-players")) {
                            if (playerTimeLeft <= 0)
                                SaveHandler.setPlayerTimeLeft(offlinePlayer, 0);
                            else
                                SaveHandler.setPlayerTimeLeft(offlinePlayer, playerTimeLeft - 1);
                        }

                        if (offlinePlayer.isOnline()) {

                            Player onlinePlayer = offlinePlayer.getPlayer();
                            if (onlinePlayer != null) {
                                onlinePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(SecondsToClockFormat.convert(playerTimeLeft, true)));
                                Bukkit.getScheduler().runTask(plugin, () -> TeamHandler.changeTeamAndGamemodeAccordingly(onlinePlayer, playerTimeLeft));
                            }

                        }

                        if (playerTimeLeft <= 0) activeTimerList.remove(offlinePlayer.getUniqueId());

                    } else {
                        Player onlinePlayer = offlinePlayer.getPlayer();
                        if (onlinePlayer != null)
                            onlinePlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(SecondsToClockFormat.convert(playerTimeLeft, true)));
                    }
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

}
