package me.rowan.limitedlife.listeners;

import me.rowan.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class InventoryEvents implements Listener {

    private final JavaPlugin plugin;

    private final List<Material> helmetTypes = new ArrayList<>();

    public InventoryEvents() {
        plugin = LimitedLife.plugin;

        helmetTypes.add(Material.LEATHER_HELMET);
        helmetTypes.add(Material.GOLDEN_HELMET);
        helmetTypes.add(Material.IRON_HELMET);
        helmetTypes.add(Material.CHAINMAIL_HELMET);
        helmetTypes.add(Material.DIAMOND_HELMET);
        helmetTypes.add(Material.NETHERITE_HELMET);
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public boolean playerIsInCreative(Inventory inv) {
        for (HumanEntity viewer : inv.getViewers()) {
            if (viewer.getGameMode() == GameMode.CREATIVE)
                return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventInventoryHelmetInteraction(InventoryClickEvent event) {
        if (!getConfig().getBoolean("recipes.helmets-unobtainable") || playerIsInCreative(event.getInventory())) return;

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
        if (!getConfig().getBoolean("recipes.helmets-unobtainable") || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
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
    public void preventPickingUpHelmets(EntityPickupItemEvent event) {
        if (!getConfig().getBoolean("recipes.helmets-unobtainable")) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (helmetTypes.contains(event.getItem().getItemStack().getType()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventInventoryBookshelfInteraction(InventoryClickEvent event) {
        if (!getConfig().getBoolean("recipes.bookshelves-unobtainable") || playerIsInCreative(event.getInventory())) return;

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
        if (!getConfig().getBoolean("recipes.bookshelves-unobtainable")) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getItem().getItemStack().getType() == Material.BOOKSHELF)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventInventoryNetheriteInteraction(InventoryClickEvent event) {
        if (!getConfig().getBoolean("recipes.netherite-unobtainable") || playerIsInCreative(event.getInventory())) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            if (currentItem.getType() == Material.NETHERITE_INGOT || currentItem.getType() == Material.NETHERITE_SCRAP) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + "Netherite is prohibited!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventPickingUpNetherite(EntityPickupItemEvent event) {
        if (!getConfig().getBoolean("recipes.netherite-unobtainable")) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (event.getItem().getItemStack().getType() == Material.NETHERITE_INGOT || event.getItem().getItemStack().getType() == Material.NETHERITE_SCRAP)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventInventoryGoldenAppleInteraction(InventoryClickEvent event) {
        if (playerIsInCreative(event.getInventory())) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem != null) {
            Material itemType = currentItem.getType();
            if (itemType == Material.GOLDEN_APPLE && getConfig().getBoolean("recipes.golden-apples-unobtainable")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + "Golden apples are prohibited!");
            } else if (itemType == Material.ENCHANTED_GOLDEN_APPLE && getConfig().getBoolean("recipes.enchanted-golden-apples-unobtainable")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.DARK_RED + "Enchanted golden apples are prohibited!");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventPickingGoldenApples(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Material materialType = event.getItem().getItemStack().getType();
        if (materialType == Material.GOLDEN_APPLE && getConfig().getBoolean("recipes.golden-apples-unobtainable"))
            event.setCancelled(true);
        else if (materialType == Material.ENCHANTED_GOLDEN_APPLE && getConfig().getBoolean("recipes.enchanted-golden-apples-unobtainable"))
            event.setCancelled(true);
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void blockBannedPotions(InventoryClickEvent event) {
        if (!getConfig().getBoolean("potion-whitelist.enabled") || playerIsInCreative(event.getInventory()))
            return;

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null)
            return;

        if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            if (potionMeta == null) return;
            PotionType potionType = potionMeta.getBasePotionData().getType();
            List<String> potionWhitelist = (List<String>) getConfig().getList("potion-whitelist.list");
            if (potionWhitelist == null) return;
            System.out.println(potionType);
            if (!potionWhitelist.contains(potionType.toString())) {
                event.setCancelled(true);
                itemStack.setType(Material.GLASS_BOTTLE);
                Inventory clickedInv = event.getClickedInventory();
                if (clickedInv == null)
                    return;
                for (HumanEntity viewer : clickedInv.getViewers()) {
                    Player player = (Player) viewer;
                    player.sendMessage(ChatColor.DARK_RED + "That potion is banned on this server!");
                }
            }
        }
    }

}
