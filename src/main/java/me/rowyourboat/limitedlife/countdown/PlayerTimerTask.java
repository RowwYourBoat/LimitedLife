package me.rowyourboat.limitedlife.countdown;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerTimerTask {

    private final JavaPlugin plugin;

    public PlayerTimerTask(OfflinePlayer offlinePlayer) {
        this.plugin = LimitedLife.plugin;
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!LimitedLife.globalTimerActive || !LimitedLife.playersActiveTimerList.contains(offlinePlayer.getUniqueId())) {this.cancel(); return;}

                long timeLeftInSeconds = LimitedLife.SaveHandler.getPlayerTimeLeft(offlinePlayer);
                if (timeLeftInSeconds <= 0)
                    LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, 0);
                else
                    LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, timeLeftInSeconds-1);

                if (offlinePlayer.isOnline()) {
                    Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
                    if (player != null)
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(SecondsToClockFormat.convert(timeLeftInSeconds, true)));

                    Bukkit.getScheduler().runTask(plugin, () -> LimitedLife.TeamHandler.changeTeamAndGamemodeAccordingly(offlinePlayer.getPlayer(), timeLeftInSeconds));
                }

                if (timeLeftInSeconds <= 0) this.cancel();
            }
        };

        timer.scheduleAtFixedRate(task, 250, 1000);
    }

}
