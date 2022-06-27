package me.towecraft.auth.listeners;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.repository.PlayerRepository;
import me.towecraft.auth.service.RecoveryService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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

    @PostConstruct
    private void init() {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)) {
            Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        event.setCancelled(true);
        ItemStack item = event.getItem();

        if (item != null)
            if (item.getType() == Material.valueOf(plugin.getConfig().getString("SMTP.recovery.item.type",
                    "WATCH"))) {
                Player player = event.getPlayer();
                recoveryService.send(player);
            }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
