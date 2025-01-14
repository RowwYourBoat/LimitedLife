package me.rowan.limitedlife.listeners;

import me.rowan.limitedlife.LimitedLife;
import me.rowan.limitedlife.data.SaveHandler;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class RevivalItemEvents implements Listener {

    private final SaveHandler SaveHandler;
    private final JavaPlugin plugin;

    private final HashMap<UUID, Integer> currentPageNumber;
    private final BukkitScheduler scheduler;

    public RevivalItemEvents() {
        this.SaveHandler = LimitedLife.SaveHandler;
        this.plugin = LimitedLife.plugin;
        this.currentPageNumber = new HashMap<>();
        this.scheduler = Bukkit.getScheduler();
    }

    public void loadNewPage(Inventory inventory, int pageNumber) {
        inventory.clear();
        loadArrows(inventory);

        List<OfflinePlayer> offlineDeadPlayers = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (SaveHandler.getPlayerTimeLeft(offlinePlayer) == 0)
                offlineDeadPlayers.add(offlinePlayer);
        }

        for (int i = 0 ; i<=17 ; i++) {
            if (offlineDeadPlayers.size() >= ((pageNumber-1)*17)+i+1) {
                OfflinePlayer offlinePlayer = offlineDeadPlayers.get(((pageNumber-1)*17)+i);
                PlayerProfile playerProfile = offlinePlayer.getPlayerProfile();
                ItemStack PLAYER_HEAD = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta SKULL_META = (SkullMeta) PLAYER_HEAD.getItemMeta();
                if (SKULL_META != null) {
                    if (playerProfile.isComplete()) SKULL_META.setOwnerProfile(playerProfile);
                    SKULL_META.setOwningPlayer(offlinePlayer);
                    SKULL_META.setDisplayName(ChatColor.YELLOW + offlinePlayer.getName());
                }
                PLAYER_HEAD.setItemMeta(SKULL_META);
                inventory.setItem(i, PLAYER_HEAD);
            }
        }
    }

    public void loadArrows(Inventory inventory) {
        ItemStack LEFT_ARROW = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta LEFT_ARROW_POTION_META = (PotionMeta) LEFT_ARROW.getItemMeta();
        if (LEFT_ARROW_POTION_META == null) return;
        LEFT_ARROW_POTION_META.setColor(Color.RED);
        LEFT_ARROW_POTION_META.setDisplayName(ChatColor.RED + "Last Page");
        LEFT_ARROW_POTION_META.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        LEFT_ARROW.setItemMeta(LEFT_ARROW_POTION_META);

        ItemStack RIGHT_ARROW = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta RIGHT_ARROW_POTION_META = (PotionMeta) RIGHT_ARROW.getItemMeta();
        if (RIGHT_ARROW_POTION_META == null) return;
        RIGHT_ARROW_POTION_META.setColor(Color.LIME);
        RIGHT_ARROW_POTION_META.setDisplayName(ChatColor.GREEN + "Next Page");
        RIGHT_ARROW_POTION_META.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
        RIGHT_ARROW.setItemMeta(RIGHT_ARROW_POTION_META);

        inventory.setItem(18, LEFT_ARROW);
        inventory.setItem(26, RIGHT_ARROW);
    }

    @EventHandler
    public void openRevivalMenuOnClockInteraction(PlayerInteractEvent event) {
        ItemStack EQUIPPED_ITEM = event.getItem();
        if (EQUIPPED_ITEM == null) return;

        ItemMeta CLOCK_ITEM_META = EQUIPPED_ITEM.getItemMeta();
        if (CLOCK_ITEM_META == null || !CLOCK_ITEM_META.getDisplayName().equalsIgnoreCase(ChatColor.LIGHT_PURPLE + "Revival Clock")) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        Inventory inventory = Bukkit.createInventory(player, 27, "Revival Menu");
        player.openInventory(inventory);

        currentPageNumber.put(player.getUniqueId(), 1);
        loadNewPage(inventory, 1);
    }

    public void removeRevivalItem(HumanEntity HUMAN_ENTITY) {
        PlayerInventory INVENTORY = HUMAN_ENTITY.getInventory();
        ItemStack itemInMainHand = INVENTORY.getItemInMainHand();
        if (itemInMainHand.getType() == Material.CLOCK &&  itemInMainHand.getAmount() >= 1)
            itemInMainHand.setAmount(itemInMainHand.getAmount()-1);

        ItemStack itemInOffHand = INVENTORY.getItemInOffHand();
        if (itemInOffHand.getType() == Material.CLOCK && itemInOffHand.getAmount() >= 1)
            itemInOffHand.setAmount(itemInOffHand.getAmount()-1);
    }

    public void teleportToWorldSpawn(Player player) {
        if (player == null) return;
        Properties pr = new Properties();
        String level_name = null;
        try {
            FileInputStream in = new FileInputStream("server.properties");
            pr.load(in);
            level_name = pr.getProperty("level-name");
            in.close();
        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage());
        }
        if (level_name == null) return;
        World world = Bukkit.getWorld(level_name);
        if (world == null) return;
        player.teleport(world.getSpawnLocation());
    }

    @EventHandler
    public void processItemInteraction(InventoryClickEvent event) {
        HumanEntity HUMAN_ENTITY = event.getWhoClicked();
        InventoryView OPEN_INVENTORY = HUMAN_ENTITY.getOpenInventory();
        if (!OPEN_INVENTORY.getTitle().equalsIgnoreCase("Revival Menu")) return;
        event.setCancelled(true);
        ItemStack CLICKED_ITEM = event.getCurrentItem();
        if (CLICKED_ITEM == null) return;
        if (CLICKED_ITEM.getType() == Material.PLAYER_HEAD) {
            SkullMeta SKULL_META = (SkullMeta) CLICKED_ITEM.getItemMeta();
            if (SKULL_META == null) return;
            OfflinePlayer SKULL_OWNER = SKULL_META.getOwningPlayer();
            if (SKULL_OWNER == null) return;
            if (LimitedLife.currentGlobalTimerTask == null) {
                scheduler.runTask(plugin, OPEN_INVENTORY::close);
                HUMAN_ENTITY.sendMessage(ChatColor.DARK_RED + "The global timer has to be active in order to revive people!");
                return;
            }

            if (SaveHandler.getPlayerTimeLeft(SKULL_OWNER) == 0) {
                if (plugin.getConfig().getBoolean("recipes.revival-item.consume-on-usage")) removeRevivalItem(HUMAN_ENTITY);
                scheduler.runTask(plugin, OPEN_INVENTORY::close);
                SaveHandler.setPlayerTimeLeft(SKULL_OWNER, plugin.getConfig().getLong("timer.seconds-on-revival"));
                LimitedLife.currentGlobalTimerTask.startPlayerTimer(SKULL_OWNER);
                HUMAN_ENTITY.playEffect(EntityEffect.TOTEM_RESURRECT);
                if (plugin.getConfig().getBoolean("recipes.revival-item.teleport-to-world-spawn")) teleportToWorldSpawn(SKULL_OWNER.getPlayer());
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    onlinePlayer.sendTitle(ChatColor.YELLOW + SKULL_OWNER.getName(), "has been revived!", 20, 100, 20);
                    if (onlinePlayer.getUniqueId() != HUMAN_ENTITY.getUniqueId())
                        onlinePlayer.playSound(onlinePlayer, Sound.ITEM_TOTEM_USE, 1, 1);
                });
            } else {
                HUMAN_ENTITY.sendMessage(ChatColor.DARK_RED + "That player is no longer dead!");
            }

        } else if (CLICKED_ITEM.getType() == Material.TIPPED_ARROW) {
            UUID HUMAN_ENTITY_UUID = HUMAN_ENTITY.getUniqueId();
            ItemMeta ITEM_META = CLICKED_ITEM.getItemMeta();
            if (ITEM_META == null) return;
            if (ITEM_META.getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Next Page")) {
                currentPageNumber.replace(HUMAN_ENTITY_UUID, currentPageNumber.get(HUMAN_ENTITY_UUID)+1);
                loadNewPage(OPEN_INVENTORY.getTopInventory(), currentPageNumber.get(HUMAN_ENTITY_UUID));
            } else if (ITEM_META.getDisplayName().equalsIgnoreCase(ChatColor.RED + "Last Page")) {
                if (currentPageNumber.get(HUMAN_ENTITY_UUID) == 1) return;
                currentPageNumber.replace(HUMAN_ENTITY_UUID, currentPageNumber.get(HUMAN_ENTITY_UUID)-1);
                loadNewPage(OPEN_INVENTORY.getTopInventory(), currentPageNumber.get(HUMAN_ENTITY_UUID));
            }
        }
    }

}