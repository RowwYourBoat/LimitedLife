package me.rowyourboat.limitedlife.commands;

import me.rowyourboat.limitedlife.commands.subcommands.CountdownCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("countdown"))
                return CountdownCommand.execute(commandSender, args);
        }
        return false;
    }
}
