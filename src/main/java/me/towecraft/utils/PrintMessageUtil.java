package me.towecraft.utils;

import me.towecraft.TAS;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import unsave.plugin.context.annotations.Autowire;

import java.util.List;

public class PrintMessageUtil {
    @Autowire
    private TAS plugin;

    public void sendMessage(Player player, String message) {
        player.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }

    public void sendMessage(Player player, List<String> messages) {
        messages.forEach(m -> player.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', m)));
    }

    public void kickMessage(Player player, String message) {
        player.kickPlayer(message);
    }
}
