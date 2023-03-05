package me.rowyourboat.limitedlife.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamHandler {

    private final Scoreboard scoreboard;

    private final Team greenName;
    private final Team yellowName;
    private final Team redName;
    private final Team grayName;

    public TeamHandler() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        scoreboard.getTeams().forEach(Team::unregister);

        greenName = scoreboard.registerNewTeam("green");
        greenName.setCanSeeFriendlyInvisibles(true);
        greenName.setColor(ChatColor.GREEN);

        yellowName = scoreboard.registerNewTeam("yellow");
        yellowName.setCanSeeFriendlyInvisibles(true);
        yellowName.setColor(ChatColor.YELLOW);

        redName = scoreboard.registerNewTeam("red");
        redName.setCanSeeFriendlyInvisibles(true);
        redName.setColor(ChatColor.RED);

        grayName = scoreboard.registerNewTeam("gray");
        grayName.setCanSeeFriendlyInvisibles(true);
        grayName.setColor(ChatColor.GRAY);
    }

    public void changeTeamAndGamemodeAccordingly(Player player, long timeLeft) {
        if (timeLeft == 0) {
            grayName.addEntry(player.getName());
            player.setGameMode(GameMode.SPECTATOR);
        } else if (timeLeft < 28800) {
            redName.addEntry(player.getName());
            player.setGameMode(GameMode.SURVIVAL);
        } else if (timeLeft < 57600) {
            yellowName.addEntry(player.getName());
            player.setGameMode(GameMode.SURVIVAL);
        } else {
            greenName.addEntry(player.getName());
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

}
