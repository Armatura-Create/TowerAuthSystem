package me.towecraft.timers;

import me.towecraft.TAS;
import me.towecraft.service.CaptchaService;
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
public class CaptchaTimer {

    @Autowire
    private TAS plugin;

    @Autowire
    private CaptchaService captchaService;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private PrintMessageService printMessageUtil;

    private Map<String, BukkitRunnable> timers;
    private long time;

    @PostConstruct
    public void init() {
        this.timers = new ConcurrentHashMap<>();
        this.time = plugin.getConfig().getInt("General.timeCaptcha", 10); //Sec
    }

    public void regTimer(Player player) {
        this.timers.put(player.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                if (timers.containsKey(player.getName())) {
                    if (captchaService.getMapActions().get(player.getName()).getCountDoneClick() < 3) {
                        captchaService.getMapActions().remove(player.getName());
                        removeTimer(player.getName());

                        if (player.isOnline())
                            printMessageUtil.kickMessage(player, fileMessages.getMSG().getString("KickMessages.youBot",
                                    "Not found string [KickMessages.youBot]"));
                    }
                }
            }
        });
        timers.get(player.getName()).runTaskLater(plugin, time * 20L);
    }

    public void removeTimer(String playerName) {
        if (timers.containsKey(playerName)) {
            timers.get(playerName).cancel();
            timers.remove(playerName);
        }
    }
}
