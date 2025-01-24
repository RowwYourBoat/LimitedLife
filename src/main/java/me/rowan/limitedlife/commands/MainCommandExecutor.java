package me.rowan.limitedlife.commands;

import me.rowan.limitedlife.LimitedLife;
import me.rowan.limitedlife.commands.subcommands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

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

    public static List<Player> getPlayersFromArgument(CommandSender sender, String argument) {
        Player playerArg = Bukkit.getPlayer(argument);
        if (playerArg != null) return new ArrayList<>(List.of(playerArg));

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<String> selectors = new ArrayList<>(Arrays.asList("@a", "@p", "@r"));
        if (!selectors.contains(argument)) return null;

        if (argument.equalsIgnoreCase("@a")) return onlinePlayers;
        if (argument.equalsIgnoreCase("@p")) {
            Location location = null;
            if (sender instanceof Player player) location = player.getLocation();
            if (sender instanceof BlockCommandSender block) location = block.getBlock().getLocation();
            if (location == null || location.getWorld() == null) return null;
            Collection<Player> playerEntities = location.getWorld().getEntitiesByClass(Player.class);
            if (playerEntities.isEmpty()) return null;
            Player closestPlayer = null;
            for (Player playerEntity : playerEntities) {
                if (closestPlayer == null || playerEntity.getLocation().distanceSquared(location) < closestPlayer.getLocation().distanceSquared(location))
                    closestPlayer = playerEntity;
            }
            return new ArrayList<>(List.of((closestPlayer)));
        }
        if (argument.equalsIgnoreCase("@r")) return new ArrayList<>(List.of(onlinePlayers.get(new Random().nextInt(onlinePlayers.size()))));

        return null;
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
