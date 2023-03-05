package me.rowyourboat.limitedlife.data;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SaveHandler {

    private File saveFile;
    private YamlConfiguration saveYaml;

    public SaveHandler() {
        saveFile = new File(LimitedLife.plugin.getDataFolder(), "save.yml");
        saveYaml = YamlConfiguration.loadConfiguration(saveFile);
    }

    private void save() {
        try {
            saveYaml.save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerTimeLeft(OfflinePlayer player, long timeToSet) {
        ConfigurationSection section = getPlayerSaveData(player);
        section.set("TimeRemaining", timeToSet);
        save();
    }

    public int getPlayerTimeLeft(OfflinePlayer player) {
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