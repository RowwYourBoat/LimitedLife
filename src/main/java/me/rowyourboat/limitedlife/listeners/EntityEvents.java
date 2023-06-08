package me.rowyourboat.limitedlife.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class EntityEvents implements Listener {

    public boolean targetDamager(EntityDamageByEntityEvent event) {
        AtomicBoolean targetted = new AtomicBoolean(false);
        Player wolfOwner = (Player) event.getEntity();
        wolfOwner.getNearbyEntities(50, 50, 50).forEach(entity -> {
            if (entity.getType() == EntityType.WOLF) {
                Wolf wolf = (Wolf) entity;
                AnimalTamer owner = wolf.getOwner();
                if (owner != null && wolf.getOwner().getUniqueId() == wolfOwner.getUniqueId()) {
                    if (!event.getDamager().isDead()) {
                        wolf.setTarget((LivingEntity) event.getDamager());
                        targetted.set(true);
                    }
                }
            }
        });
        return targetted.get();
    }

    public void targetDamaged(EntityDamageByEntityEvent event) {
        Player wolfOwner = (Player) event.getDamager();
        wolfOwner.getNearbyEntities(50, 50, 50).forEach(entity -> {
            if (entity.getType() == EntityType.WOLF) {
                Wolf wolf = (Wolf) entity;
                AnimalTamer owner = wolf.getOwner();
                if (owner != null && wolf.getOwner().getUniqueId() == wolfOwner.getUniqueId()) {
                    if (!event.getEntity().isDead()) {
                        wolf.setTarget((LivingEntity) event.getEntity());
                    }
                }
            }
        });
    }

    @EventHandler
    public void wolfDisrespectTeams(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.PLAYER || event.getDamager().getType() != EntityType.PLAYER) return;
        if (!targetDamager(event))
            targetDamaged(event);
    }

}
