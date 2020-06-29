package me.towecraft.utils.timers;

import me.towecraft.TAS;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class CaptchaTimer {
    private HashMap<String, BukkitRunnable> timers;
    private long time;

    public CaptchaTimer() {
        this.timers = new HashMap<>();
        this.time = 7; //Sec
    }

    public void logTimer(final Player player) {
        this.timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (CaptchaTimer.this.timers.containsKey(player.getName())) {
                    if (TAS.captchaListener.getCountDoneClick().get(player.getName()) != null &&
                            TAS.captchaListener.getCountDoneClick().get(player.getName()) < 3) {
                        TAS.captchaListener.getCountDoneClick().remove(player.getName());
                        TAS.captchaListener.getCountMissClick().remove(player.getName());
                        CaptchaTimer.this.timers.get(player.getName()).cancel();
                        CaptchaTimer.this.timers.remove(player.getName());
                        player.kickPlayer(TAS.files.getMSG().getString("KickMessages.captcha"));
                    }
                }
            }
        });
        timers.get(player.getName()).runTaskLater(TAS.plugin, time * 20);
    }

    public HashMap<String, BukkitRunnable> getTimers() {
        return this.timers;
    }
}
