package me.rowan.limitedlife.scoreboard;

import me.rowan.limitedlife.LimitedLife;
import me.rowan.limitedlife.discord.RequestHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TeamHandler {

    private final FileConfiguration config;
    private final Scoreboard scoreboard;

    private final Team darkGreenName;
    private final Team greenName;
    private final Team yellowName;
    private final Team redName;
    private final Team grayName;

    public TeamHandler() {

        JavaPlugin plugin = LimitedLife.plugin;
        this.config = plugin.getConfig();

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) plugin.getLogger().severe("Unable to locate the scoreboard manager! Have you initialized a world yet? Please restart the server to fix this.");
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        scoreboard.getTeams().forEach(Team::unregister);

        darkGreenName = scoreboard.registerNewTeam("dark_green");
        darkGreenName.setCanSeeFriendlyInvisibles(false);
        darkGreenName.setColor(ChatColor.DARK_GREEN);

        greenName = scoreboard.registerNewTeam("green");
        greenName.setCanSeeFriendlyInvisibles(false);
        greenName.setColor(ChatColor.GREEN);

        yellowName = scoreboard.registerNewTeam("yellow");
        yellowName.setCanSeeFriendlyInvisibles(false);
        yellowName.setColor(ChatColor.YELLOW);

        redName = scoreboard.registerNewTeam("red");
        redName.setCanSeeFriendlyInvisibles(false);
        redName.setColor(ChatColor.RED);

        grayName = scoreboard.registerNewTeam("gray");
        grayName.setCanSeeFriendlyInvisibles(false);
        grayName.setColor(ChatColor.GRAY);

        Bukkit.getOnlinePlayers().forEach(player -> changeTeamAndGamemodeAccordingly(player, LimitedLife.SaveHandler.getPlayerTimeLeft(player)));

    }

    public void changeTeamAndGamemodeAccordingly(Player player, long timeLeft) {

        RequestHandler requestHandler = LimitedLife.RequestHandler;

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
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers())
                        onlinePlayer.sendTitle(ChatColor.RED + player.getName(), "ran out of time!", 20, 100, 20);
                    if (config.getBoolean("other.lightning-strike-on-final-death"))
                        player.getWorld().strikeLightningEffect(player.getLocation());
                }
                LimitedLife.SaveHandler.markPlayerAsDead(player);

                requestHandler.addToRoleUpdateQueue(player.getUniqueId(), "gray");

            }

        } else if (timeLeft < config.getInt("name-colour-thresholds.red-name")) {

            if (!redName.hasEntry(player.getName())) {

                redName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
                requestHandler.addToRoleUpdateQueue(player.getUniqueId(), "red");

            }

        } else if (timeLeft < config.getInt("name-colour-thresholds.yellow-name")) {

            if (!yellowName.hasEntry(player.getName())) {

                yellowName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
                requestHandler.addToRoleUpdateQueue(player.getUniqueId(), "yellow");

            }

        } else if ((config.getBoolean("name-colour-thresholds.dark-green-names") && timeLeft < config.getInt("name-colour-thresholds.green-name")) || !config.getBoolean("name-colour-thresholds.dark-green-names")) {

            if (!greenName.hasEntry(player.getName())) {

                greenName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
                requestHandler.addToRoleUpdateQueue(player.getUniqueId(), "green");

            }

        } else {

            if (!darkGreenName.hasEntry(player.getName())) {

                darkGreenName.addEntry(player.getName());
                player.setGameMode(GameMode.SURVIVAL);
                LimitedLife.SaveHandler.removePlayerDeathMark(player);
                requestHandler.addToRoleUpdateQueue(player.getUniqueId(), "dark_green");

            }

        }
    }

    public void clearTeamMembers() {
        Bukkit.getOnlinePlayers().forEach(plr -> scoreboard.getTeams().forEach(team -> team.removeEntry(plr.getName())));
    }

}