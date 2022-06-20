package me.towecraft.auth.listeners;

import me.towecraft.auth.TAS;
import me.towecraft.auth.command.RecoveryPasswordCommand;
import me.towecraft.auth.service.PlayerService;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.service.RecoveryService;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.utils.MatcherUtil;
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

    @Autowire
    private RecoveryService recoveryService;

    @Autowire
    private PrintMessageService printMessage;

    private boolean isCaptcha;

    @PostConstruct
    public void init() {
        isCaptcha = plugin.getConfig().getInt("Captcha.type", 0) > 0;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        String name = player.getName();

        List<String> excludeSymbols = plugin.getConfig().getStringList("Username.excludeSymbol");

        if (MatcherUtil.checkContainsRusSymbol(name) ||
                MatcherUtil.checkRusSymbol(name)
                || name.length() < 3
                || name.length() > 16
                || excludeSymbols.stream().anyMatch(name::contains)) {

            printMessage.kickMessage(player,
                    fileMessages.getMSG()
                    .getString("KickMessages.incorrectName", "Not found string [KickMessages.incorrectName]"));
            return;
        }

        player.getInventory().clear();

        recoveryService.getItem(player);
        playerService.verify(player, isCaptcha);
    }
}
