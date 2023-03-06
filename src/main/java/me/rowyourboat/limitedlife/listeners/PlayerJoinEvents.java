package me.rowyourboat.limitedlife.listeners;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEvents implements Listener {

    @EventHandler
    public void setTeamOnJoin(PlayerJoinEvent event) {
        LimitedLife.TeamHandler.changeTeamAndGamemodeAccordingly(event.getPlayer(), LimitedLife.SaveHandler.getPlayerTimeLeft(event.getPlayer()));
    }

}
