package me.rowyourboat.limitedlife.data;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SaveHandler {

    private final JavaPlugin plugin;

    private final File saveFile;
    private final YamlConfiguration saveYaml;

    public SaveHandler() {
        this.plugin = LimitedLife.plugin;
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
                saveYaml.set("plugin-timer", plugin.getConfig().getLong("timer.start-time-in-seconds"));

            save();
        }
    }

    public void save() {
        try {
            saveYaml.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
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

    public void subtractPlayerTime(OfflinePlayer player, long timeToSubtract) {
        long timeLeft = getPlayerTimeLeft(player);
        if (timeLeft-timeToSubtract<0) {
            setPlayerTimeLeft(player, 0);
            return;
        }
        setPlayerTimeLeft(player, timeLeft-timeToSubtract);
    }

    public void addPlayerTime(OfflinePlayer player, long timeToAdd) {
        long timeLeft = getPlayerTimeLeft(player);
        setPlayerTimeLeft(player, timeLeft+timeToAdd);
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

    public void cureBoogeyman(String boogeymanToCureUUID) {
        getBoogeymenList().remove(boogeymanToCureUUID);
        Player player = Bukkit.getPlayer(UUID.fromString(boogeymanToCureUUID));
        if (player != null) {
            player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "You've been cured!", null, 10, 40, 10);
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 3, 1);
        }
        save();
    }

    public void cureAllBoogeymen() {
        getBoogeymenList().forEach(uuidString -> {
            Player player = Bukkit.getPlayer(uuidString);
            if (player != null) {
                player.sendTitle(ChatColor.GREEN + ChatColor.BOLD.toString() + "You've been cured!", null, 10, 40, 10);
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 3, 1);
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
                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_DEATH, 10, 1);

                long timeLeft = LimitedLife.SaveHandler.getPlayerTimeLeft(player);
                if (timeLeft > plugin.getConfig().getInt("name-colour-thresholds.yellow-name"))
                    LimitedLife.SaveHandler.setPlayerTimeLeft(player, plugin.getConfig().getInt("name-colour-thresholds.yellow-name"));
                else if (timeLeft > plugin.getConfig().getInt("name-colour-thresholds.red-name"))
                    LimitedLife.SaveHandler.setPlayerTimeLeft(player, plugin.getConfig().getInt("name-colour-thresholds.red-name"));
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
        if (time > plugin.getConfig().getLong("name-colour-thresholds.yellow-name"))
            return "GREEN";
        else if (time > plugin.getConfig().getLong("name-colour-thresholds.red-name"))
            return "YELLOW";
        else
            return "RED";
    }

    @SuppressWarnings("unchecked")
    public List<String> getMarkedAsDeadList() {
        return (List<String>) saveYaml.getList("marked-as-dead-list");
    }

    public ConfigurationSection getPlayerSaveData(OfflinePlayer player) {
        String uuidString = player.getUniqueId().toString();
        if (saveYaml.getConfigurationSection(uuidString) != null)
            return saveYaml.getConfigurationSection(uuidString);
        else {
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