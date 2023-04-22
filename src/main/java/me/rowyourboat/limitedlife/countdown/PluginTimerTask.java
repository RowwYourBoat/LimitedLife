package me.rowyourboat.limitedlife.countdown;

import me.rowyourboat.limitedlife.LimitedLife;

import java.util.Timer;
import java.util.TimerTask;

public class PluginTimerTask {

    public PluginTimerTask() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!LimitedLife.globalTimerActive) {this.cancel(); return;}
                LimitedLife.SaveHandler.countDownPluginSecond();
            }
        };

        timer.scheduleAtFixedRate(task, 250, 1000);
    }

}
