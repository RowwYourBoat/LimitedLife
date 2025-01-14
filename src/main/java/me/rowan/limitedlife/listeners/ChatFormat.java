package me.rowan.limitedlife.listeners;

import me.rowan.limitedlife.LimitedLife;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatFormat implements Listener {

    private final JavaPlugin plugin;

    public ChatFormat() {
        plugin = LimitedLife.plugin;
    }

    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void applyChatFormat(AsyncPlayerChatEvent event) {
        if (!getConfig().getBoolean("custom-chat-format.enabled")) return;
        String newFormat = getConfig().getString("custom-chat-format.format");
        if (newFormat == null) return;
        if (!newFormat.contains("player") || !newFormat.contains("message")) {
            plugin.getLogger().severe("The custom chat format hasn't been set up correctly!\nMake sure to include 'player' and 'message' in the format!");
            return;
        }
        newFormat = newFormat.replace("player", "%1$s");
        newFormat = newFormat.replace("message", "%2$s");
        event.setFormat(newFormat);
    }

}
