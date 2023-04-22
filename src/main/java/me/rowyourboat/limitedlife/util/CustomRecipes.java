package me.rowyourboat.limitedlife.util;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class CustomRecipes {

    private final List<NamespacedKey> namespacedKeyList;

    public CustomRecipes() {
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

    }

    public void grant(Player player) {
        player.discoverRecipes(namespacedKeyList);
    }

}
