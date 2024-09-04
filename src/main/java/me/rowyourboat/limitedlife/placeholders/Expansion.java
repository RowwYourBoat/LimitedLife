package me.rowyourboat.limitedlife.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rowyourboat.limitedlife.LimitedLife;
import me.rowyourboat.limitedlife.data.SaveHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class Expansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "limitedlife";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Rowan";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String param) {

        SaveHandler saveHandler = LimitedLife.SaveHandler;

        if (param.equalsIgnoreCase("boogeymen")) {
            return parseBoogeymanList(saveHandler);
        } else if (param.equalsIgnoreCase("dead")) {
            return parseMarkedAsDeadList(saveHandler);
        } else if (player != null) {

            long timeLeft = saveHandler.getPlayerTimeLeft(player);
            if (param.equalsIgnoreCase("time"))
                return String.valueOf(timeLeft);
            else if (param.equalsIgnoreCase("color"))
                return saveHandler.convertTimeToTeamName(timeLeft);

        }

        return null;
    }

    private @Nullable String parseBoogeymanList(SaveHandler saveHandler) {
        StringBuilder parsed = new StringBuilder();
        List<String> boogeymenList = saveHandler.getBoogeymenList();
        return getString(parsed, boogeymenList);
    }

    private @Nullable String parseMarkedAsDeadList(SaveHandler saveHandler) {
        StringBuilder parsed = new StringBuilder();
        List<String> markedAsDeadList = saveHandler.getMarkedAsDeadList();
        return getString(parsed, markedAsDeadList);
    }

    @Nullable
    private String getString(StringBuilder parsed, List<String> uuidList) {
        if (uuidList.isEmpty()) return null;
        for (int i = 0; i < uuidList.size(); i++) {
            UUID uuid = UUID.fromString(uuidList.get(i));
            if (i == 0)
                parsed.append(Bukkit.getOfflinePlayer(uuid).getName());
            else
                parsed.append(",").append(Bukkit.getOfflinePlayer(uuid).getName());
        }
        return parsed.toString();
    }

}