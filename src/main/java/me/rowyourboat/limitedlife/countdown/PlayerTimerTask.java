package me.rowyourboat.limitedlife.countdown;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.commands.subcommands.TimerCommand;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerTimerTask {

    public PlayerTimerTask(OfflinePlayer offlinePlayer) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!LimitedLife.globalTimerActive || TimerCommand.uuidTimerDisabledList.contains(offlinePlayer.getUniqueId())) this.cancel();

                long timeLeftInSeconds = LimitedLife.SaveHandler.getPlayerTimeLeft(offlinePlayer);
                if (timeLeftInSeconds <= 0)
                    LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, 0);
                else
                    LimitedLife.SaveHandler.setPlayerTimeLeft(offlinePlayer, timeLeftInSeconds-1);

                if (offlinePlayer.isOnline()) {
                    Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
                    if (player != null)
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(SecondsToClockFormat.convert(timeLeftInSeconds, true)));

                    Bukkit.getScheduler().runTask(LimitedLife.plugin, () -> LimitedLife.TeamHandler.changeTeamAndGamemodeAccordingly(offlinePlayer.getPlayer(), timeLeftInSeconds));
                }

                if (timeLeftInSeconds <= 0) this.cancel();
            }
        };

        timer.scheduleAtFixedRate(task, 250, 1000);
    }

}
