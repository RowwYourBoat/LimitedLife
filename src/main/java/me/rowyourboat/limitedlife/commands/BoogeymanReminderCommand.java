package me.rowyourboat.limitedlife.commands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.data.SaveHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public class BoogeymanReminderCommand implements CommandExecutor {

    private final SaveHandler SaveHandler;
    private final JavaPlugin plugin;

    public BoogeymanReminderCommand() {
        this.SaveHandler = LimitedLife.SaveHandler;
        this.plugin = LimitedLife.plugin;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(sender instanceof Player)) return true;
        if (!plugin.getConfig().getBoolean("other.am-i-the-boogeyman-command")) { sender.sendMessage(ChatColor.DARK_RED + "This command has been disabled."); return true; }

        Player player = (Player) sender;
        if (SaveHandler.getBoogeymenList().contains(player.getUniqueId().toString()))
            player.sendMessage(ChatColor.RED + "You are " + ChatColor.BOLD + "The Boogeyman" + ChatColor.RESET + ChatColor.RED + "!");
        else
            player.sendMessage(ChatColor.GREEN + "You are " + ChatColor.BOLD + "NOT" + ChatColor.RESET + ChatColor.GREEN + " The Boogeyman!");
        return true;
    }

}
