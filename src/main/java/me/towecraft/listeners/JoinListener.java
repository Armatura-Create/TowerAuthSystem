package me.towecraft.listeners;

import me.towecraft.TAS;
import me.towecraft.service.PlayerService;
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

import java.util.List;

@Component
public class JoinListener implements Listener {

    @Autowire
    private TAS plugin;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private PlayerService playerService;

    private boolean isCaptcha;

    @PostConstruct
    public void init() {
        isCaptcha = plugin.getConfig().getInt("General.captchaType", 0) > 0;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        String name = player.getName();

        List<String> excludeSymbols = plugin.getConfig().getStringList("Username.excludeSymbol");

        if (name.matches("^[А-ЯЁа-яё]*")
                || name.length() < 3
                || name.length() > 16
                || name.matches("^\\w*")
                || excludeSymbols.stream().anyMatch(name::contains)) {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', fileMessages
                    .getMSG()
                    .getString("KickMessages.incorrectName", "Not found string [KickMessages.IncorrectName]")));
            return;
        }

        playerService.verify(player, isCaptcha);
    }
}
