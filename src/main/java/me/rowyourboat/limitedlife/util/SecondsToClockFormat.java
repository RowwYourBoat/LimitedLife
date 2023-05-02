package me.rowyourboat.limitedlife.util;

import me.rowyourboat.limitedlife.LimitedLife;
import org.bukkit.ChatColor;

public class SecondsToClockFormat {

    // https://youtu.be/weJHt-Jf0uE?list=LL
    public static String convert(long TimeLeftInSeconds, boolean applyColor) {
        final int MINUTES_IN_HOUR = 60;
        final int SECONDS_IN_MINUTE = 60;
        long second = TimeLeftInSeconds;
        long minute;
        long hour;

        minute = second/SECONDS_IN_MINUTE;
        second -= minute*SECONDS_IN_MINUTE;

        hour = minute/MINUTES_IN_HOUR;
        minute -= hour*MINUTES_IN_HOUR;

        String hourString = String.valueOf(hour);
        if (hourString.length() == 1)
            hourString = "0"+hourString;

        String minuteString = String.valueOf(minute);
        if (minuteString.length() == 1)
            minuteString = "0"+minuteString;

        String secondString = String.valueOf(second);
        if (secondString.length() == 1)
            secondString = "0"+secondString;

        if (applyColor) {
            ChatColor color;
            if (TimeLeftInSeconds == 0)
                color = ChatColor.GRAY;
            else if (TimeLeftInSeconds < LimitedLife.plugin.getConfig().getInt("name-colour-thresholds.red-name"))
                color = ChatColor.RED;
            else if (TimeLeftInSeconds < LimitedLife.plugin.getConfig().getInt("name-colour-thresholds.yellow-name"))
                color = ChatColor.YELLOW;
            else if ((LimitedLife.plugin.getConfig().getBoolean("name-colour-thresholds.dark-green-names") && TimeLeftInSeconds < LimitedLife.plugin.getConfig().getInt("name-colour-thresholds.green-name")) || !LimitedLife.plugin.getConfig().getBoolean("name-colour-thresholds.dark-green-names"))
                color = ChatColor.GREEN;
            else
                color = ChatColor.DARK_GREEN;

            String finalString = color + ChatColor.BOLD.toString() + hourString + ":" + minuteString + ":" + secondString;
            if (finalString.contains("-"))
                return ChatColor.GRAY + ChatColor.BOLD.toString() + "00:00:00";
            return finalString;
        }

        return hourString + ":" + minuteString + ":" + secondString;
    }

}
