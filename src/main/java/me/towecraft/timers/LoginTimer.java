package me.towecraft.timers;

import me.towecraft.TAS;
import me.towecraft.utils.FileMessages;
import me.towecraft.service.PrintMessageService;
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
    private PrintMessageService printMessageUtil;

    @Autowire
    private FileMessages fileMessages;

    private Map<String, BukkitRunnable> timers;
    private int time;

    @PostConstruct
    public void init() {
        this.timers = new ConcurrentHashMap<>();
        this.time = plugin.getConfig().getInt("General.timeLogin", 30); //Sec
    }

    public void regTimer(Player player) {
        timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (LoginTimer.this.timers.containsKey(player.getName())) {
                    removeTimer(player.getName());
                    if (player.isOnline())
                        printMessageUtil.kickMessage(player, fileMessages.getMSG().getString("KickMessages.timeoutAuth",
                                "Not found string [KickMessages.timeoutAuth]"));
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

    public void removeTimer(String playerName) {
        if (timers.containsKey(playerName)) {
            timers.get(playerName).cancel();
            timers.remove(playerName);
        }
    }
}
