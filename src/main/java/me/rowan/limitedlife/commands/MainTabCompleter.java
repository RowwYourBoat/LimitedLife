package me.rowan.limitedlife.commands;

import me.rowan.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainTabCompleter implements TabCompleter {

    private List<String> getBoogeymanNames() {
        List<String> arguments = new ArrayList<>();
        List<String> boogeymenUUIDStrings = LimitedLife.SaveHandler.getBoogeymenList();
        for (String uuidString : boogeymenUUIDStrings)
            arguments.add(Bukkit.getOfflinePlayer(UUID.fromString(uuidString)).getName());
        return arguments;
    }

    private List<String> getModifyTimeArgs(String[] args) {
        List<String> arguments = new ArrayList<>();
        String timeArg = args[2];
        if (timeArg.isEmpty()) {
            arguments.add("-");
            arguments.add("+");
        } else if (timeArg.length() == 1 && (timeArg.equalsIgnoreCase("-") || timeArg.equalsIgnoreCase("+"))) {
            arguments.add("num");
        } else if (timeArg.length() >= 2) {
            arguments.add("h");
            arguments.add("m");
            arguments.add("s");
        }
        return arguments;
    }

    private List<String> getOfflinePlayers() {
        List<String> arguments = new ArrayList<>();
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            arguments.add(offlinePlayer.getName());
        }
        return arguments;
    }

    private List<String> stringToList(String string) {
        String[] splitString = string.split(",");
        return new ArrayList<>(Arrays.asList(splitString));
    }

    private List<String> getPlayerNamesAndSelectors() {
        List<String> arguments = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> arguments.add(player.getName()));
        List<String> selectors = new ArrayList<>(Arrays.asList("@a", "@p", "@r"));
        arguments.addAll(selectors);
        return arguments;
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (args.length == 1)
            return stringToList("timer,boogeyman,modifytime,gettime,reload,help");
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("timer"))
                return stringToList("start,pause,reset");
            else if (args[0].equalsIgnoreCase("boogeyman"))
                return stringToList("roll,cure,clear,punish,cancel");
            else if (args[0].equalsIgnoreCase("modifytime"))
                return getPlayerNamesAndSelectors();
            else if (args[0].equalsIgnoreCase("gettime"))
                return getOfflinePlayers();
            else if (args[0].equalsIgnoreCase("help"))
                return stringToList("boogeyman,modifytime,timer");
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("timer"))
                return null;
            if (args[1].equalsIgnoreCase("cure"))
                return getBoogeymanNames();
            else if (args[0].equalsIgnoreCase("modifytime"))
                return getModifyTimeArgs(args);
            else if (args[0].equalsIgnoreCase("boogeyman") && args[1].equalsIgnoreCase("roll"))
                return stringToList("skiprolldelay");
        }

        return new ArrayList<>();
    }
}
