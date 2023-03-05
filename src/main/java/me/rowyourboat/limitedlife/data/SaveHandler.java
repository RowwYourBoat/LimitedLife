package me.rowyourboat.limitedlife.data;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class SaveHandler {

    private File saveFile;
    private YamlConfiguration saveYaml;

    public SaveHandler() {
        saveFile = new File(LimitedLife.plugin.getDataFolder(), "data" + File.separator + "save.yml");
        saveYaml = YamlConfiguration.loadConfiguration(saveFile);
    }

    public void save() {
        try {
            saveYaml.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBoogeymen(List<UUID> playerUUIDList) {
        saveYaml.set("Boogeymen", playerUUIDList);
        save();
    }

    public void cureBoogeyman(UUID boogeymanToCure) {

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

    @SuppressWarnings("unchecked")
    public List<UUID> getBoogeymenList() {
        if (!saveYaml.contains("Boogeymen")) return null;
        return (List<UUID>) saveYaml.getList("Boogeymen");
    }

    public long getPlayerTimeLeft(OfflinePlayer player) {
        return getPlayerSaveData(player).getInt("TimeRemaining");
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

}