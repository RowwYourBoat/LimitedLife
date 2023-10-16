package me.rowyourboat.limitedlife.discord;

import com.google.gson.Gson;
import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class RequestHandler {

    private final Logger logger;

    private final HashMap<UUID, String> roleUpdateQueue;

    private String authKey;
    private URI uri;

    public RequestHandler() {

        logger = LimitedLife.plugin.getLogger();
        roleUpdateQueue = new HashMap<>();
        FileConfiguration config = LimitedLife.plugin.getConfig();

        if (!config.getBoolean("discord-integration.enabled")) return;

        long guildId = config.getLong("discord-integration.server-id");
        String tempURI = config.getString("discord-integration.endpoint");
        authKey = getAuthKey(config);

        if (guildId == 0 || tempURI == null) {
            logger.severe("[DISCORD] Discord Integration is set to enabled, but has not been set up correctly.");
            return;
        }

        try {
            uri = new URI(tempURI + "/" + guildId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        startQueueProcessing();

    }

    public void addToRoleUpdateQueue(UUID playerUUID, String colour) {
        roleUpdateQueue.put(playerUUID, colour);
    }

    // Send one role update request every 1.5s
    private void startQueueProcessing() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(LimitedLife.plugin, () -> {

            UUID uuidToRemove = null;
            for (UUID uuid : roleUpdateQueue.keySet()) {
                String colour = roleUpdateQueue.get(uuid);
                uuidToRemove = uuid;
                sendRoleUpdateRequest(uuid, colour);
                break;
            }
            if (uuidToRemove != null)
                roleUpdateQueue.remove(uuidToRemove);

        }, 0, 30);

    }

    private void sendRoleUpdateRequest(UUID playerUUID, String colour) {

        // Build request json body
        Transcript transcript = new Transcript();
        transcript.setUser_uuid(playerUUID.toString().replaceAll("-", ""));
        transcript.setColour(colour);

        Gson gson = new Gson();
        String json = gson.toJson(transcript);

        // Build request
        HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(uri)
            .header("authorization", authKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        // Send post request asynchronously
        HttpClient httpClient = HttpClient.newHttpClient();
        Bukkit.getScheduler().runTaskAsynchronously(LimitedLife.plugin, () -> {

            HttpResponse<String> response;
            try {
                response = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                logger.severe("[DISCORD] " + e.getMessage());
                logger.warning("[DISCORD] Something went wrong while trying to communicate with the integration bot.");
                logger.warning("[DISCORD] This could mean that either the bot is down or that you've edited the config incorrectly.");
                return;
            }

            if (response.statusCode() == 201)
                logger.info("[DISCORD] " + response.body());
            else
                logger.warning("[DISCORD] " + response.body());

        });

    }

    // Return stored unique key or generate one
    private String getAuthKey(FileConfiguration config) {
        String key = config.getString("discord-integration.key");
        if (key != null)
            return key;
        else {
            UUID randomUUID = UUID.randomUUID();
            config.set("discord-integration.key", randomUUID.toString());
            LimitedLife.plugin.saveConfig();
            return randomUUID.toString();
        }
    }

}
