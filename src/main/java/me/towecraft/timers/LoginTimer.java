package me.towecraft.timers;

import me.towecraft.TAS;
import me.towecraft.utils.FileMessages;
import me.towecraft.utils.PrintMessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginTimer {

    @Autowire
    private TAS plugin;

    @Autowire
    private PrintMessageUtil printMessageUtil;

    @Autowire
    private FileMessages fileMessages;

    private Map<String, BukkitRunnable> timers;
    private int time;

    @PostConstruct
    public void init() {
        this.timers = new ConcurrentHashMap<>();
        this.time = plugin.getConfig().getInt("General.timeLogin", 30); //Sec
    }

    public void logTimer(Player player) {
        timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (LoginTimer.this.timers.containsKey(player.getName())) {
                    if (player.isOnline()) {
                        LoginTimer.this.timers.get(player.getName()).cancel();
                        LoginTimer.this.timers.remove(player.getName());
                        printMessageUtil.kickMessage(player, fileMessages.getMSG().getString("KickMessages.login"));
                    } else {
                        LoginTimer.this.timers.get(player.getName()).cancel();
                        LoginTimer.this.timers.remove(player.getName());
                    }
                }
            }
        });
        timers.get(player.getName()).runTaskLater(plugin, time * 20L);

        player.setLevel(time);
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setLevel(player.getLevel() - 1);
            }
        }.runTaskTimer(plugin, 40L, 20L);
    }

    public Map<String, BukkitRunnable> getTimers() {
        return this.timers;
    }

    public void removeTimer(String playerName) {
        if (timers.containsKey(playerName)) {
            timers.get(playerName).cancel();
            timers.remove(playerName);
        }
    }
}
