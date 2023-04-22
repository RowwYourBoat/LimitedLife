package me.rowyourboat.limitedlife.listeners;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.countdown.PlayerTimerTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEvents implements Listener {

    @EventHandler
    public void setTeamOnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        long timeLeft = LimitedLife.SaveHandler.getPlayerTimeLeft(player);
        if (timeLeft == -1 && LimitedLife.globalTimerActive && !LimitedLife.playersActiveTimerList.contains(player.getUniqueId())) {

            if (LimitedLife.plugin.getConfig().getBoolean("timer.balanced-time-for-latecomers"))
                LimitedLife.SaveHandler.setPlayerTimeLeft(player, LimitedLife.SaveHandler.getPluginTimeRemaining());
            else LimitedLife.SaveHandler.setPlayerTimeLeft(player, LimitedLife.plugin.getConfig().getInt("timer.start-time-in-hours")*(long)Math.pow(60, 2));

            LimitedLife.playersActiveTimerList.add(player.getUniqueId());
            new PlayerTimerTask(player);
        } else if (LimitedLife.globalTimerActive && !LimitedLife.playersActiveTimerList.contains(player.getUniqueId())) {
            LimitedLife.playersActiveTimerList.add(player.getUniqueId());
            new PlayerTimerTask(player);
        }
    }

    @EventHandler
    public void grantRecipesOnJoin(PlayerJoinEvent event) {
        LimitedLife.CustomRecipes.grant(event.getPlayer());
    }

}
