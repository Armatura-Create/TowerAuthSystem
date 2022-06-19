package me.towecraft.auth.timers;

import me.towecraft.auth.TAS;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.database.repository.PlayerRepository;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegisterTimer {

    @Autowire
    private TAS plugin;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private PrintMessageService printMessage;
    @Autowire
    private FileMessages fileMessages;

    private Map<String, BukkitRunnable> timers;
    private int time;

    @PostConstruct
    public void init() {
        this.timers = new ConcurrentHashMap<>();
        this.time = plugin.getConfig().getInt("General.timeReg", 30); //Sec
    }

    public void regTimer(Player player) {
        timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (RegisterTimer.this.timers.containsKey(player.getName())) {
                    try {
                        playerRepository.findByUsername(player.getName(), result -> {
                            removeTimer(player.getName());
                            if (!result.isPresent()) {
                                printMessage.kickMessage(player, fileMessages.getMSG().getString("KickMessages.timeoutAuth",
                                        "Not found string [KickMessages.timeoutAuth]"));
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
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

    public void removeTimer(String playerName) {
        if (timers.containsKey(playerName)) {
            timers.get(playerName).cancel();
            timers.remove(playerName);
        }
    }
}
