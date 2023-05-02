package me.rowyourboat.limitedlife.util;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomRecipes {

    private final List<NamespacedKey> namespacedKeyList;

    public CustomRecipes() {
        FileConfiguration config = LimitedLife.plugin.getConfig();

        namespacedKeyList = new ArrayList<>();
        if (LimitedLife.plugin.getConfig().getBoolean("recipes.craftable-name-tag")) {
            ShapelessRecipe name_tagShapelessRecipe = new ShapelessRecipe(new NamespacedKey(LimitedLife.plugin, "craftable_name_tag"), new ItemStack(Material.NAME_TAG));
            name_tagShapelessRecipe.addIngredient(Material.STRING);
            name_tagShapelessRecipe.addIngredient(Material.PAPER);
            Bukkit.addRecipe(name_tagShapelessRecipe);
            namespacedKeyList.add(name_tagShapelessRecipe.getKey());
        }

        if (LimitedLife.plugin.getConfig().getBoolean("recipes.craftable-saddle")) {
            ShapedRecipe craftable_saddleShapedRecipe = new ShapedRecipe(new NamespacedKey(LimitedLife.plugin, "craftable_saddle"), new ItemStack(Material.SADDLE));
            craftable_saddleShapedRecipe.shape(
                    "XXX",
                    "XLX",
                    "LXL"
            );
            craftable_saddleShapedRecipe.setIngredient('L', Material.LEATHER);
            craftable_saddleShapedRecipe.setIngredient('X', Material.AIR);
            Bukkit.addRecipe(craftable_saddleShapedRecipe);
            namespacedKeyList.add(craftable_saddleShapedRecipe.getKey());
        }

        if (LimitedLife.plugin.getConfig().getBoolean("recipes.paper-tnt")) {
            ShapedRecipe paper_tntShapedRecipe = new ShapedRecipe(new NamespacedKey(LimitedLife.plugin, "paper_tnt"), new ItemStack(Material.TNT));
            paper_tntShapedRecipe.shape(
                    "PSP",
                    "SGS",
                    "PSP"
            );
            paper_tntShapedRecipe.setIngredient('P', Material.PAPER);
            paper_tntShapedRecipe.setIngredient('S', Material.SAND);
            paper_tntShapedRecipe.setIngredient('G', Material.GUNPOWDER);
            Bukkit.addRecipe(paper_tntShapedRecipe);
            namespacedKeyList.add(paper_tntShapedRecipe.getKey());
        }

        if (LimitedLife.plugin.getConfig().getBoolean("recipes.craftable-slimeball")) {
            ShapedRecipe slimeballShapedRecipe = new ShapedRecipe(new NamespacedKey(LimitedLife.plugin, "craftable_slimeball"), new ItemStack(Material.SLIME_BALL, 3));
            slimeballShapedRecipe.shape(
                    "XBX",
                    "BSB",
                    "XWX"
            );
            slimeballShapedRecipe.setIngredient('B', Material.BONE_MEAL);
            slimeballShapedRecipe.setIngredient('S', Material.SUGAR_CANE);
            slimeballShapedRecipe.setIngredient('W', Material.WATER_BUCKET);
            slimeballShapedRecipe.setIngredient('X', Material.AIR);
            Bukkit.addRecipe(slimeballShapedRecipe);
            namespacedKeyList.add(slimeballShapedRecipe.getKey());
        }

        if (LimitedLife.plugin.getConfig().getBoolean("recipes.revival-item.enabled")) {
            ItemStack REVIVAL_ITEM = new ItemStack(Material.CLOCK);
            ItemMeta ITEM_META = REVIVAL_ITEM.getItemMeta();
            if (ITEM_META != null) {
                ITEM_META.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
                ITEM_META.setDisplayName(ChatColor.LIGHT_PURPLE + "Revival Clock");
                List<String> LORE = new ArrayList<>();
                LORE.add(ChatColor.GRAY + "Right click while holding the item to open the Revival Menu!");
                ITEM_META.setLore(LORE);
                REVIVAL_ITEM.setItemMeta(ITEM_META);
                REVIVAL_ITEM.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
            }

            ShapedRecipe revival_itemShapedRecipe = new ShapedRecipe(new NamespacedKey(LimitedLife.plugin, "revival_item"), REVIVAL_ITEM);
            revival_itemShapedRecipe.shape(
                    "ABC",
                    "DEF",
                    "GHI"
            );
            String rowOne = config.getString("recipes.revival-item.row1");
            String rowTwo = config.getString("recipes.revival-item.row2");
            String rowThree = config.getString("recipes.revival-item.row3");
            if (rowOne == null || rowTwo == null || rowThree == null) return;

            HashMap<Character, Material> materialHashMap = new HashMap<>();
            materialHashMap.put('A', Material.getMaterial(rowOne.split(" ")[0]));
            materialHashMap.put('B', Material.getMaterial(rowOne.split(" ")[1]));
            materialHashMap.put('C', Material.getMaterial(rowOne.split(" ")[2]));
            materialHashMap.put('D', Material.getMaterial(rowTwo.split(" ")[0]));
            materialHashMap.put('E', Material.getMaterial(rowTwo.split(" ")[1]));
            materialHashMap.put('F', Material.getMaterial(rowTwo.split(" ")[2]));
            materialHashMap.put('G', Material.getMaterial(rowThree.split(" ")[0]));
            materialHashMap.put('H', Material.getMaterial(rowThree.split(" ")[1]));
            materialHashMap.put('I', Material.getMaterial(rowThree.split(" ")[2]));

            for (Character character : materialHashMap.keySet()) {
                if (materialHashMap.get(character) != null) {
                    revival_itemShapedRecipe.setIngredient(character, materialHashMap.get(character));
                } else {
                    Bukkit.getLogger().severe("Invalid item specified in the revival item recipe!");
                }
            }

            Bukkit.addRecipe(revival_itemShapedRecipe);
            namespacedKeyList.add(revival_itemShapedRecipe.getKey());
        }

    }

    public void grant(Player player) {
        player.discoverRecipes(namespacedKeyList);
    }

}
