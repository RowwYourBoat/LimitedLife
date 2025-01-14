package me.rowan.limitedlife.listeners;

import me.rowan.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EnchantmentLimitations implements Listener {

    private final JavaPlugin plugin;

    public EnchantmentLimitations() {
        this.plugin = LimitedLife.plugin;
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
    public void preventCraftingEnchantmentTable(CraftItemEvent event) {
        if (getConfig().getBoolean("enchantment-table.craftable") || playerIsInCreative(event.getInventory())) return;

        if (event.getRecipe().getResult().getType() == Material.ENCHANTING_TABLE)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void preventBreakingEnchantmentTable(BlockBreakEvent event) {
        if (getConfig().getBoolean("enchantment-table.breakable") || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;

       if (event.getBlock().getType() == Material.ENCHANTING_TABLE)
           event.setCancelled(true);
    }

    @EventHandler
    public void makeEnchantmentTableIndestructibleOnDrop(PlayerDropItemEvent event) {
        if (!getConfig().getBoolean("enchantment-table.indestructible-on-drop")) return;

        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.getType() == Material.ENCHANTING_TABLE){
            Item item = event.getItemDrop();
            item.setCustomName("Invulnerable");
            item.setUnlimitedLifetime(true);
            item.setInvulnerable(true);
        }
    }

    @EventHandler
    public void preventExplodingOfEnchantmentTable(EntityDamageByEntityEvent event){
        if (!getConfig().getBoolean("enchantment-table.indestructible-on-drop")) return;

        for (Entity entity : event.getDamager().getNearbyEntities(10,10,10)) {
            if (entity.getType() == EntityType.DROPPED_ITEM){
                if (entity.getCustomName() != null) {
                    if (entity.getCustomName().equalsIgnoreCase("Invulnerable")) {
                        event.setCancelled(true);
                    }
                }
            }
        }

    }



    HashMap<UUID, Map<Enchantment, Integer>> justGotNerfed = new HashMap<>();
    public void nerfEnchantmentLevel(Map<Enchantment, Integer> enchantments, List<HumanEntity> viewers, ItemStack resultItem, PrepareAnvilEvent event) {
        StringBuilder nerfedEnchantmentsMessage = new StringBuilder(ChatColor.DARK_RED + "The following enchantments have been nerfed:\n" + ChatColor.GRAY);
        Map<Enchantment, Integer> nerfedEnchantments = new HashMap<>();
        ItemStack nerfedItem = new ItemStack(resultItem.getType());

        ItemMeta resultItemMeta = resultItem.getItemMeta();
        if (resultItemMeta == null) return;
        if (resultItemMeta.hasDisplayName()){
            ItemMeta nerfedItemMeta = nerfedItem.getItemMeta();
            if (nerfedItemMeta == null) return;
            nerfedItemMeta.setDisplayName(resultItemMeta.getDisplayName());
            nerfedItem.setItemMeta(nerfedItemMeta);
        }

        if (resultItem.getType() != Material.ENCHANTED_BOOK) {
            Damageable resultItemDamageAble = (Damageable) resultItem.getItemMeta();
            Damageable nerfedItemDamageAble = (Damageable) nerfedItem.getItemMeta();
            if (nerfedItemDamageAble == null) return;
            nerfedItemDamageAble.setDamage(resultItemDamageAble.getDamage());
            nerfedItem.setItemMeta(nerfedItemDamageAble);
        }

        int levelLimit = getConfig().getInt("anvil.level-limit");
        for (Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if (enchantment.getValue() > levelLimit) {
                nerfedEnchantments.put(enchantment.getKey(), levelLimit);
                nerfedEnchantmentsMessage.append(enchantment.getKey().getKey().toString().replace("minecraft:", "")).append(", ");
            } else {
                nerfedEnchantments.put(enchantment.getKey(), enchantment.getValue());
            }
        }
        nerfedItem.addUnsafeEnchantments(nerfedEnchantments);
        event.setResult(nerfedItem);

        for (HumanEntity viewer : viewers) {
            UUID viewerUUID = viewer.getUniqueId();
            if (justGotNerfed.containsKey(viewerUUID))
                break;

            justGotNerfed.put(viewerUUID, nerfedEnchantments);
            Bukkit.getScheduler().runTaskLater(plugin, () -> justGotNerfed.remove(viewerUUID), 60);
            if (!nerfedEnchantmentsMessage.toString().equals(ChatColor.DARK_RED + "The following enchantments have been nerfed:\n" + ChatColor.GRAY))
                viewer.sendMessage(String.valueOf(nerfedEnchantmentsMessage));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void modifyIllegalEnchantments(PrepareAnvilEvent event) {
        if (!plugin.getConfig().getBoolean("anvil.stacking-nerf") || playerIsInCreative(event.getInventory()))
            return;

        ItemStack resultItem = event.getResult();
        if (resultItem == null)
            return;

        if (resultItem.getType() == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) resultItem.getItemMeta();
            if (enchantmentStorageMeta != null) {
                Map<Enchantment, Integer> enchantments = enchantmentStorageMeta.getStoredEnchants();
                nerfEnchantmentLevel(enchantments, event.getInventory().getViewers(), resultItem, event);
            }
        } else {
            ItemMeta itemMeta = resultItem.getItemMeta();
            if (itemMeta != null){
                if (!itemMeta.getEnchants().isEmpty()){
                    Map<Enchantment, Integer> enchantments = itemMeta.getEnchants();
                    nerfEnchantmentLevel(enchantments, event.getInventory().getViewers(), resultItem, event);
                }
            }
        }
    }

}
