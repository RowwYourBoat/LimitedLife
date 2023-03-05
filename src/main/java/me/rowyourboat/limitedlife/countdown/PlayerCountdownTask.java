package me.rowyourboat.limitedlife.countdown;

import me.rowyourboat.limitedlife.LimitedLife;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerCountdownTask {

    public PlayerCountdownTask(OfflinePlayer offlinePlayer) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!LimitedLife.countdownActive) this.cancel();

                long timeLeftInSeconds = LimitedLife.SaveHandler.getPlayerTimeLeft(offlinePlayer);
                LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, timeLeftInSeconds-1);
                if (offlinePlayer.isOnline()) {
                    Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
                    if (player != null)
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(getClockString(timeLeftInSeconds)));

                    Bukkit.getScheduler().runTask(LimitedLife.plugin, () -> LimitedLife.TeamHandler.changeTeamAndGamemodeAccordingly(offlinePlayer.getPlayer(), timeLeftInSeconds));
                }

                if (timeLeftInSeconds <= 0) this.cancel();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    // https://youtu.be/weJHt-Jf0uE?list=LL
    private String getClockString(long TimeLeftInSeconds) {
        final int MINUTES_IN_HOUR = 60;
        final int SECONDS_IN_MINUTE = 60;
        long second = TimeLeftInSeconds;
        long minute;
        long hour;

        minute = second/SECONDS_IN_MINUTE;
        second -= minute*SECONDS_IN_MINUTE;

        hour = minute/MINUTES_IN_HOUR;
        minute -= hour*MINUTES_IN_HOUR;

        String hourString = String.valueOf(hour);
        if (hourString.length() == 1)
            hourString = "0"+hourString;

        String minuteString = String.valueOf(minute);
        if (minuteString.length() == 1)
            minuteString = "0"+minuteString;

        String secondString = String.valueOf(second);
        if (secondString.length() == 1)
            secondString = "0"+secondString;

        return ChatColor.GREEN + ChatColor.BOLD.toString() + hourString + ":" + minuteString + ":" + secondString;
    }

}
