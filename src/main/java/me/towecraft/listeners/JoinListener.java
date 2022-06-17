package me.towecraft.listeners;

import me.towecraft.TAS;
import me.towecraft.utils.FileMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

@Component
public class JoinListener implements Listener {

    @Autowire
    private TAS plugin;

    @Autowire
    private FileMessages fileMessages;

    @PostConstruct
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        String name = player.getName();

        if (name.matches("^[А-ЯЁа-яё]*")
                || name.length() < 3
                || name.length() > 16
                || name.matches("^\\w*")
                || name.contains("$")
                || name.contains(" ")
                || name.contains("-")) {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', fileMessages
                    .getMSG()
                    .getString("KickMessages.incorrectName", "Not found string [KickMessages.IncorrectName]")));
            return;
        }


    }
}
