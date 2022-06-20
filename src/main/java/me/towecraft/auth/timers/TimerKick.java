package me.towecraft.auth.timers;

import org.bukkit.entity.Player;

public interface TimerKick {

    void regTimer(Player player);
    void removeTimer(Player player);

}
