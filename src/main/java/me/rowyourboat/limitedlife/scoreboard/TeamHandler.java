package me.rowyourboat.limitedlife.scoreboard;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamHandler {

    private final Scoreboard scoreboard;

    private final Team greenName;
    private final Team yellowName;
    private final Team redName;
    private final Team grayName;

    public TeamHandler() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) LimitedLife.plugin.getLogger().severe("Unable to locate the scoreboard manager! Have you initialized a world yet?");
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

        Bukkit.getOnlinePlayers().forEach(player -> changeTeamAndGamemodeAccordingly(player, LimitedLife.SaveHandler.getPlayerTimeLeft(player)));
    }

    public void changeTeamAndGamemodeAccordingly(Player player, long timeLeft) {
        if (timeLeft <= -1) {
            scoreboard.getTeams().forEach(team -> {
                if (team.hasEntry(player.getName()))
                    team.removeEntry(player.getName());
            });
            player.setGameMode(GameMode.ADVENTURE);
            LimitedLife.SaveHandler.removePlayerDeathMark(player);
        } else if (timeLeft == 0) {
            if (!grayName.hasEntry(player.getName())) {
                grayName.addEntry(player.getName());
                player.setGameMode(GameMode.SPECTATOR);
                if (!LimitedLife.SaveHandler.getMarkedAsDeadList().contains(player.getUniqueId().toString())) {
                    player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10, 1);
                    Bukkit.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + player.getName() + " ran out of time!");
                }
                LimitedLife.SaveHandler.markPlayerAsDead(player);
            }
        } else if (timeLeft < LimitedLife.plugin.getConfig().getInt("name-colour-thresholds.red-name")) {
            if (!redName.hasEntry(player.getName())) {
                redName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        } else if (timeLeft < LimitedLife.plugin.getConfig().getInt("name-colour-thresholds.yellow-name")) {
            if (!yellowName.hasEntry(player.getName())) {
                yellowName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        } else {
            if (!greenName.hasEntry(player.getName())) {
                greenName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
            }
        }
    }

}