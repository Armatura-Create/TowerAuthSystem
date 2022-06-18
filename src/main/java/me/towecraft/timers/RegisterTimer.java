package me.towecraft.timers;

import me.towecraft.TAS;
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
                        MySQL.isPlayerDB(player, new CallbackSQL<Boolean>() {
                            @Override
                            public void done(Boolean data) {
                                if (data) {
                                    RegisterTimer.this.timers.get(player.getName()).cancel();
                                    RegisterTimer.this.timers.remove(player.getName());
                                } else {
                                    RegisterTimer.this.timers.get(player.getName()).cancel();
                                    RegisterTimer.this.timers.remove(player.getName());
                                    player.kickPlayer(TAS.getPrefix() + TAS.files.getMSG().getString("KickMessages.register"));
                                }
                            }

                            @Override
                            public void error(Exception ex) {
                                TAS.log(ex.getMessage());
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
