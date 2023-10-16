package me.rowyourboat.limitedlife.data;

import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.util.SecondsToClockFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SaveHandler {

    private final FileConfiguration config;

    private final File saveFile;
    private final YamlConfiguration saveYaml;

    public SaveHandler() {
        JavaPlugin plugin = LimitedLife.plugin;
        this.config = plugin.getConfig();
        
        saveFile = new File(plugin.getDataFolder(), "data" + File.separator + "save.yml");
        saveYaml = YamlConfiguration.loadConfiguration(saveFile);
        if (!saveYaml.contains("marked-as-dead-list")) {
            saveYaml.set("marked-as-dead-list", new ArrayList<String>());
            save();
        }
        if (!saveYaml.contains("boogeymen")) {
            saveYaml.set("boogeymen", new ArrayList<String>());
            save();
        }
        if (!saveYaml.contains("plugin-timer")) {

            boolean foundPlayerData = false;
            String mostTimeRemainingUUIDString = null;
            for (String str : saveYaml.getValues(false).keySet()) {
                if (saveYaml.isConfigurationSection(str)) {
                    foundPlayerData = true;
                    if (mostTimeRemainingUUIDString == null)
                        mostTimeRemainingUUIDString = str;
                    else {
                        ConfigurationSection currentPlayerData = saveYaml.getConfigurationSection(str);
                        ConfigurationSection bestPlayerData = saveYaml.getConfigurationSection(mostTimeRemainingUUIDString);
                        if (currentPlayerData != null && bestPlayerData != null) {
                            if (currentPlayerData.getLong("TimeRemaining") > bestPlayerData.getLong("TimeRemaining"))
                                mostTimeRemainingUUIDString = str;
                        }
                    }
                }
            }

            if (foundPlayerData) {
                ConfigurationSection bestPlayerData = saveYaml.getConfigurationSection(mostTimeRemainingUUIDString);
                if (bestPlayerData != null)
                    saveYaml.set("plugin-timer", bestPlayerData.getLong("TimeRemaining"));
            } else
                saveYaml.set("plugin-timer", config.getLong("timer.start-time-in-seconds"));

            save();
        }
    }

    public void save() {
        try {
            saveYaml.save(saveFile);
        } catch (IOException e) {
            LimitedLife.plugin.getLogger().severe("Something went wrong while attempting to save:");
            LimitedLife.plugin.getLogger().severe(e.getMessage());
        }
    }

    public void sendTimeChangeTitle(Player player, String title) {
        if (player == null) return;
        player.sendTitle(title, null, 10, 50, 10);
    }

    public void setPlayerTimeLeft(OfflinePlayer player, long timeToSet) {
        ConfigurationSection section = getPlayerSaveData(player);
        section.set("TimeRemaining", timeToSet);
    }

    public void subtractPlayerTime(OfflinePlayer offlinePlayer, long timeToSubtract, boolean showTitle) {
        long timeLeft = getPlayerTimeLeft(offlinePlayer);
        if (timeLeft-timeToSubtract<0) {
            setPlayerTimeLeft(offlinePlayer, 0);
            return;
        }
        setPlayerTimeLeft(offlinePlayer, timeLeft-timeToSubtract);

        Player onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer != null && showTitle)
            sendTimeChangeTitle(onlinePlayer, ChatColor.RED + "-" + SecondsToClockFormat.convert(timeToSubtract, false));
    }

    public void addPlayerTime(OfflinePlayer offlinePlayer, long timeToAdd) {
        long timeLeft = getPlayerTimeLeft(offlinePlayer);
        setPlayerTimeLeft(offlinePlayer, timeLeft+timeToAdd);

        Player onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer != null)
            sendTimeChangeTitle(onlinePlayer, ChatColor.GREEN + "+" + SecondsToClockFormat.convert(timeToAdd, false));
    }

    public void markPlayerAsDead(OfflinePlayer player) {
        String uuidString = player.getUniqueId().toString();
        if (!getMarkedAsDeadList().contains(uuidString))
            getMarkedAsDeadList().add(uuidString);
        save();
    }

    public void removePlayerDeathMark(OfflinePlayer player) {
        String uuidString = player.getUniqueId().toString();
        if (getMarkedAsDeadList().contains(uuidString))
            if (LimitedLife.currentGlobalTimerTask != null) {
                LimitedLife.currentGlobalTimerTask.startPlayerTimer(player);
            }
        getMarkedAsDeadList().remove(uuidString);
        save();
    }

    public void setBoogeymen(List<UUID> playerUUIDList) {
        saveYaml.set("boogeymen", UUIDListToStringList(playerUUIDList));
        save();
    }

    public void cureBoogeyman(String boogeymanToCureUUID, boolean awardTime) {
        getBoogeymenList().remove(boogeymanToCureUUID);
        Player player = Bukkit.getPlayer(UUID.fromString(boogeymanToCureUUID));
        if (awardTime) {
            addPlayerTime(Bukkit.getOfflinePlayer(UUID.fromString(boogeymanToCureUUID)), config.getLong("boogeyman.time-gain-on-boogey-kill"));
            if (player != null)
                player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "You've been cured!", ChatColor.GREEN + "+" + SecondsToClockFormat.convert(config.getLong("boogeyman.time-gain-on-boogey-kill"), false), 10, 40, 10);
        }
        if (!awardTime && player != null) {
            player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "You've been cured!", null, 10, 40, 10);
            if (config.getBoolean("sound-effects.enabled"))
                player.playSound(player, Sound.valueOf(config.getString("sound-effects.boogeyman-cured")), 1, 1);
        }
        save();
    }

    public void cureAllBoogeymen() {
        getBoogeymenList().forEach(uuidString -> {
            Player player = Bukkit.getPlayer(uuidString);
            if (player != null) {
                player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "You've been cured!", null, 10, 40, 10);
                if (config.getBoolean("sound-effects.enabled"))
                    player.playSound(player, Sound.valueOf(config.getString("sound-effects.boogeyman-cured")), 1, 1);
            }
        });
        getBoogeymenList().clear();
        save();
    }

    public void punishBoogeymen() {
        getBoogeymenList().forEach(uuidString -> {
            Player player = Bukkit.getPlayer(UUID.fromString(uuidString));
            if (player != null) {
                player.sendTitle(ChatColor.RED + ChatColor.BOLD.toString() + "You have failed!", ChatColor.GRAY + "Your time has been lowered to the next colour!", 10, 60, 10);
                if (config.getBoolean("sound-effects.enabled"))
                    player.playSound(player, Sound.valueOf(config.getString("sound-effects.boogeyman-failed")), 1, 1);

                long timeLeft = LimitedLife.SaveHandler.getPlayerTimeLeft(player);
                if (timeLeft > config.getInt("name-colour-thresholds.yellow-name"))
                    LimitedLife.SaveHandler.setPlayerTimeLeft(player, config.getInt("name-colour-thresholds.yellow-name"));
                else if (timeLeft > config.getInt("name-colour-thresholds.red-name"))
                    LimitedLife.SaveHandler.setPlayerTimeLeft(player, config.getInt("name-colour-thresholds.red-name"));
                else
                    LimitedLife.SaveHandler.setPlayerTimeLeft(player, 0);
            }
        });
        getBoogeymenList().clear();
        save();
    }

    public void countDownPluginSecond() {
        saveYaml.set("plugin-timer", getPluginTimeRemaining()-1);
    }

    public long getPluginTimeRemaining() {
        return saveYaml.getLong("plugin-timer");
    }

    @SuppressWarnings("unchecked")
    public List<String> getBoogeymenList() {
        if (!saveYaml.contains("boogeymen")) return new ArrayList<>();
        return (List<String>) saveYaml.getList("boogeymen");
    }

    public long getPlayerTimeLeft(OfflinePlayer player) {
        return getPlayerSaveData(player).getInt("TimeRemaining");
    }


    public String convertTimeToTeamName(long time) {
        if (time > config.getLong("name-colour-thresholds.yellow-name"))
            return "GREEN";
        else if (time > config.getLong("name-colour-thresholds.red-name"))
            return "YELLOW";
        else
            return "RED";
    }

    @SuppressWarnings("unchecked")
    public List<String> getMarkedAsDeadList() {
        return (List<String>) saveYaml.getList("marked-as-dead-list");
    }

    public OfflinePlayer getPlayerByName(String name) {
        OfflinePlayer finalOfflinePlayer = null;

        Set<String> values = saveYaml.getValues(false).keySet();
        for (String value : values) {
            if (saveYaml.isConfigurationSection(value)) {
                ConfigurationSection playerSaveData = saveYaml.getConfigurationSection(value);
                if (playerSaveData != null) {
                    String nameValue = playerSaveData.getString("Name");
                    if (nameValue != null) {
                        if (nameValue.equalsIgnoreCase(name)) {
                            finalOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(value));
                            break;
                        }
                    }
                }
            }
        }

        return finalOfflinePlayer;
    }

    public ConfigurationSection getPlayerSaveData(OfflinePlayer player) {
        String uuidString = player.getUniqueId().toString();
        ConfigurationSection playerSaveData = saveYaml.getConfigurationSection(uuidString);
        if (playerSaveData != null) {
            String nameValue = playerSaveData.getString("Name");
            if (nameValue == null || !nameValue.equalsIgnoreCase(player.getName()))
                playerSaveData.set("Name", player.getName());
            return playerSaveData;
        } else {
            ConfigurationSection newSection = saveYaml.createSection(uuidString);
            newSection.set("TimeRemaining", -1);
            return newSection;
        }
    }

    private List<String> UUIDListToStringList(List<UUID> uuidList) {
        List<String> stringList = new ArrayList<>();
        uuidList.forEach(uuid -> stringList.add(uuid.toString()));
        return stringList;
    }

}