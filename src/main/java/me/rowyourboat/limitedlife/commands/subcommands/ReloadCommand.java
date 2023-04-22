package me.rowyourboat.limitedlife.commands.subcommands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.commands.MainCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    public static boolean execute(CommandSender sender) {
        LimitedLife.reloadPlugin(sender);
        sender.sendMessage(ChatColor.GRAY + "Reloading plugin..");
        MainCommandExecutor.commandFeedback(sender, "Reloaded the plugin");
        return true;
    }

}
