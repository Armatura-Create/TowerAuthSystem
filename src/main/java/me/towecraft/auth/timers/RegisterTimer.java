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
    private Map<String, BukkitRunnable> timeLevels;
    private int time;

    @PostConstruct
    public void init() {
        this.timers = new ConcurrentHashMap<>();
        this.timeLevels = new ConcurrentHashMap<>();
        this.time = plugin.getConfig().getInt("General.timeReg", 30); //Sec
    }

    public void regTimer(Player player) {
        timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (timers.containsKey(player.getName())) {
                    try {
                        removeTimer(player.getName());
                        playerRepository.findByUsername(player.getName(), result -> {
                            if (!result.isPresent() && player.isOnline()) {
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
        BukkitRunnable timeLevel = new BukkitRunnable() {
            @Override
            public void run() {
                player.setLevel(player.getLevel() - 1);
            }
        };
        timeLevel.runTaskTimer(plugin, 0, 20L);
        timeLevels.put(player.getName(), timeLevel);
    }

    public void removeTimer(String playerName) {
        if (timers.containsKey(playerName)) {
            timers.get(playerName).cancel();
            timers.remove(playerName);
        }
        if (timeLevels.containsKey(playerName)) {
            timeLevels.get(playerName).cancel();
            timeLevels.remove(playerName);
        }
    }
}
