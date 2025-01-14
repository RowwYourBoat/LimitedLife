package me.rowan.limitedlife.commands.subcommands;

import me.rowan.limitedlife.LimitedLife;
import me.rowan.limitedlife.commands.MainCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    public static boolean execute(CommandSender sender) {
        LimitedLife.reloadPlugin(sender);
        sender.sendMessage(ChatColor.GRAY + "Reloading plugin..");
        MainCommandExecutor.commandFeedback(sender, "Reloaded the Limited Life plugin");
        return true;
    }

}
