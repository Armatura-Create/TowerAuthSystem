package me.towecraft.utils;

import me.towecraft.TAS;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import unsave.plugin.context.annotations.Autowire;

public class PrintMessageChatUtil {
    @Autowire
    private TAS plugin;

    public void sendMessage(Player player, String message) {
        player.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }
}
