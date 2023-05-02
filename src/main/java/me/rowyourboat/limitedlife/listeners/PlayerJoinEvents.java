package me.rowyourboat.limitedlife.listeners;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEvents implements Listener {

    @EventHandler
    public void setTeamOnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        long timeLeft = LimitedLife.SaveHandler.getPlayerTimeLeft(player);
        if (timeLeft == -1 && LimitedLife.currentGlobalTimerTask != null && !LimitedLife.currentGlobalTimerTask.playerHasActiveTimer(player)) {

            if (LimitedLife.plugin.getConfig().getBoolean("timer.balanced-time-for-latecomers"))
                LimitedLife.SaveHandler.setPlayerTimeLeft(player, LimitedLife.SaveHandler.getPluginTimeRemaining());
            else LimitedLife.SaveHandler.setPlayerTimeLeft(player, LimitedLife.plugin.getConfig().getInt("timer.start-time-in-seconds"));

            LimitedLife.currentGlobalTimerTask.startPlayerTimer(player);
        } else if (LimitedLife.currentGlobalTimerTask != null && !LimitedLife.currentGlobalTimerTask.playerHasActiveTimer(player)) {
            LimitedLife.currentGlobalTimerTask.startPlayerTimer(player);
        }
    }

    @EventHandler
    public void grantRecipesOnJoin(PlayerJoinEvent event) {
        LimitedLife.CustomRecipes.grant(event.getPlayer());
    }

}
