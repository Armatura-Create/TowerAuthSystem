package me.towecraft.utils.timers;

import me.towecraft.TAS;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class LoginTimer {
    private final HashMap<String, BukkitRunnable> timers;
    private final int time;

    public LoginTimer() {
        this.timers = new HashMap<>();
        this.time = 30; //Sec
    }

    public void logTimer(final Player player) {
        timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (LoginTimer.this.timers.containsKey(player.getName())) {
                    if (player.isOnline()) {
                        LoginTimer.this.timers.get(player.getName()).cancel();
                        LoginTimer.this.timers.remove(player.getName());
                        player.kickPlayer(TAS.files.getMSG().getString("KickMessages.login"));
                    } else {
                        LoginTimer.this.timers.get(player.getName()).cancel();
                        LoginTimer.this.timers.remove(player.getName());
                    }
                }
            }
        });
        timers.get(player.getName()).runTaskLater(TAS.plugin, time * 20);

        player.setLevel(time);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setLevel(player.getLevel() - 1);
            }
        }.runTaskTimer(TAS.plugin, 40L, 20L);
    }

    public HashMap<String, BukkitRunnable> getTimers() {
        return this.timers;
    }
}
