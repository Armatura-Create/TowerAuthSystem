package me.towecraft.timers;

import me.towecraft.TAS;
import me.towecraft.utils.callbacks.CallbackSQL;
import me.towecraft.utils.mysql.MySQL;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class RegisterTimer {
    private final HashMap<String, BukkitRunnable> timers;
    private final int time;

    public RegisterTimer() {
        this.timers = new HashMap<>();
        this.time = 30; //Sec
    }

    public void regTimer(final Player player) {
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
        timers.get(player.getName()).runTaskLater(TAS.plugin, time * 20L);

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
