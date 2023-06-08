package me.rowyourboat.limitedlife.commands;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.commands.subcommands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MainCommandExecutor implements CommandExecutor {

    public static void commandFeedback(CommandSender sender, String str) {
        if (!LimitedLife.plugin.getConfig().getBoolean("other.command-feedback")) return;

        String finalMessage = ChatColor.GRAY + ChatColor.ITALIC.toString() + "[" + sender.getName() + ": " + str + "]";
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("limitedlife.admin") && !player.getName().equalsIgnoreCase(sender.getName()))
                player.sendMessage(finalMessage);
        }
        if (!(sender instanceof ConsoleCommandSender))
            Bukkit.getConsoleSender().sendMessage(finalMessage);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("timer"))
                return TimerCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("boogeyman"))
                return BoogeymanCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("modifytime"))
                return ModifyTimeCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("gettime"))
                return GetTimeCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("reload"))
                return ReloadCommand.execute(commandSender);
            else if (args[0].equalsIgnoreCase("help"))
                return HelpCommand.execute(commandSender, args);
        }
        return false;
    }
}
