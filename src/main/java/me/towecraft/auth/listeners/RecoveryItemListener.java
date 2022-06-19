package me.towecraft.auth.listeners;

import me.towecraft.auth.TAS;
import me.towecraft.auth.service.RecoveryService;
import me.towecraft.auth.database.repository.PlayerAuthRepository;
import me.towecraft.auth.database.repository.PlayerRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

@Component
public class RecoveryItemListener implements Listener {

    @Autowire
    private TAS plugin;

    @Autowire
    private RecoveryService recoveryService;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private PlayerAuthRepository playerAuthRepository;

    @PostConstruct
    private void init() {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)) {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Восстановление пароля")) {
            Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);

            playerRepository.findByUsername(player.getName(), result -> result.ifPresent(p -> {
                String code = getRandomCode();
                p.getPlayerAuth().setRecoveryCode(getRandomCode());
                playerAuthRepository.saveRecovery(p.getPlayerAuth());
                recoveryService.send(p.getEmail(), "", code, p.getUsername());
            }));
        }
    }

    private String getRandomCode() {
        return null;
    }
}
