package me.rowyourboat.limitedlife.listeners;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class InventoryEvents implements Listener {

    private final JavaPlugin plugin;

    public InventoryEvents() {
        plugin = LimitedLife.plugin;
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventCraftingAndFindingHelmets(InventoryClickEvent event) {
        if (!getConfig().getBoolean("recipes.disable-helmets")) return;

        List<Material> helmetTypes = new ArrayList<>();
        helmetTypes.add(Material.LEATHER_HELMET);
        helmetTypes.add(Material.GOLDEN_HELMET);
        helmetTypes.add(Material.IRON_HELMET);
        helmetTypes.add(Material.CHAINMAIL_HELMET);
        helmetTypes.add(Material.DIAMOND_HELMET);
        helmetTypes.add(Material.NETHERITE_HELMET);

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            if (helmetTypes.contains(currentItem.getType())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + "Helmets are prohibited!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventEquippingHelmets(PlayerInteractEvent event) {
        if (!getConfig().getBoolean("recipes.disable-helmets")) return;

        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Bukkit.getScheduler().runTaskLater(LimitedLife.plugin, () -> {
                if (player.isOnline()) {
                    ItemStack helmet = player.getInventory().getHelmet();
                    if (helmet != null) {
                        helmet.setAmount(0);
                        player.sendMessage(ChatColor.DARK_RED + "Helmets are prohibited!");
                    }
                }
            }, 5);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventCraftingBookshelves(InventoryClickEvent event) {
        if (!getConfig().getBoolean("recipes.disable-bookshelves")) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            if (currentItem.getType() == Material.BOOKSHELF) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + "Bookshelves are prohibited!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventPickingUpBookshelves(EntityPickupItemEvent event) {
        if (!getConfig().getBoolean("recipes.disable-bookshelves")) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getItem().getItemStack().getType() == Material.BOOKSHELF)
            event.setCancelled(true);
    }

}
