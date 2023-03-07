package me.rowyourboat.limitedlife.commands;

import me.rowyourboat.limitedlife.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MainCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("timer"))
                return TimerCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("boogeyman"))
                return BoogeymanCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("modifytime"))
                return ModifyTimeCommand.execute(commandSender, args);
            else if (args[0].equalsIgnoreCase("reload"))
                return ReloadCommand.execute(commandSender);
            else if (args[0].equalsIgnoreCase("help"))
                return HelpCommand.execute(commandSender, args);
        }
        return false;
    }
}
