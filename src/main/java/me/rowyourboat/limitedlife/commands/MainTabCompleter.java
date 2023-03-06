package me.rowyourboat.limitedlife.commands;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainTabCompleter implements TabCompleter {

    public List<String> getSubCommands() {
        List<String> arguments = new ArrayList<>();
        arguments.add("timer");
        arguments.add("boogeyman");
        arguments.add("settime");
        return arguments;
    }

    public List<String> getTimerCommands() {
        List<String> arguments = new ArrayList<>();
        arguments.add("start");
        arguments.add("pause");
        arguments.add("reset");
        return arguments;
    }

    public List<String> getBoogeymanCommands() {
        List<String> arguments = new ArrayList<>();
        arguments.add("roll");
        arguments.add("cure");
        arguments.add("clear");
        arguments.add("punish");
        return arguments;
    }

    public List<String> getBoogeymanNames() {
        List<String> arguments = new ArrayList<>();
        List<String> boogeymenUUIDStrings = LimitedLife.SaveHandler.getBoogeymenList();
        for (String uuidString : boogeymenUUIDStrings)
            arguments.add(Bukkit.getOfflinePlayer(UUID.fromString(uuidString)).getName());
        return arguments;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (args.length == 1)
            return getSubCommands();
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("timer"))
                return getTimerCommands();
            else if (args[0].equalsIgnoreCase("boogeyman"))
                return getBoogeymanCommands();
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("timer"))
                return null;
            if (args[1].equalsIgnoreCase("cure"))
                return getBoogeymanNames();
        }

        return new ArrayList<>();
    }
}
