package me.towecraft.auth.timers;

import me.towecraft.auth.TAS;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.service.PrintMessageService;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginTimer /*implements TimerKick*/ {

    @Autowire
    private TAS plugin;

    @Autowire
    private PrintMessageService printMessage;

    @Autowire
    private FileMessages fileMessages;

    private Map<Player, BukkitRunnable> timers;
    private Map<Player, BukkitRunnable> timeLevels;
    private int time;

    @PostConstruct
    public void init() {
        this.timers = new ConcurrentHashMap<>();
        this.timeLevels = new ConcurrentHashMap<>();
        this.time = plugin.getConfig().getInt("General.timeLogin", 30); //Sec
    }

//    @Override
    public void regTimer(Player player) {
        timers.put(player, new BukkitRunnable() {
            @Override
            public void run() {
                if (timers.containsKey(player)) {
                    removeTimer(player);
                    if (player.isOnline()) {
                        printMessage.kickMessage(player.getPlayer(), fileMessages.getMSG().getString("KickMessages.timeoutAuth",
                                "Not found string [KickMessages.timeoutAuth]"));
                    }
                }
            }
        });
        timers.get(player).runTaskLater(plugin, time * 20L);

        player.setLevel(time);
        BukkitRunnable timeLevel = new BukkitRunnable() {
            @Override
            public void run() {
                player.setLevel(player.getLevel() - 1);
            }
        };
        timeLevel.runTaskTimer(plugin, 0, 20L);

        timeLevels.put(player, timeLevel);
    }

//    @Override
    public void removeTimer(Player player) {
        if (timers.containsKey(player)) {
            timers.get(player).cancel();
            timers.remove(player);
        }
        if (timeLevels.containsKey(player)) {
            timeLevels.get(player).cancel();
            timeLevels.remove(player);
        }
    }
}
