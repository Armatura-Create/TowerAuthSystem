package me.towecraft.auth.listeners.captcha;

import me.towecraft.auth.TAS;
import me.towecraft.auth.service.CaptchaService;
import me.towecraft.auth.service.PlayerService;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.service.PrintMessageService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

@Component
public class CaptchaListener implements Listener {

    @Autowire
    private TAS plugin;
    @Autowire
    private CaptchaService captchaService;

    @Autowire
    private PlayerService playerService;

    @Autowire
    private PrintMessageService printMessage;

    @Autowire
    private FileMessages fileMessages;


    @PostConstruct
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.getPlayer() != null && e.getPlayer() instanceof Player) {
                    Player player = (Player) e.getPlayer();
                    if (player.isOnline()) {
                        if (captchaService.getMapActions().get(e.getPlayer().getName()) != null &&
                                captchaService.getMapActions().get(e.getPlayer().getName()).getCountDoneClick() < captchaService.getCountDone()) {
                            if (captchaService.getTypeCaptcha(player) != TypeCaptcha.NONE) {
                                captchaService.showCaptcha(player);
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e != null && e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            CaptchaModel captchaModel = captchaService.getMapActions().get(player.getName());

            if (e.getSlot() >= 0) {
                ItemStack item = e.getInventory().getItem(e.getSlot());

                if (item != null &&
                        !(e.getInventory().getItem(e.getSlot()).getType() == Material.SLIME_BLOCK)) {
                    ItemStack done = new ItemStack(Material.SLIME_BLOCK, 1);
                    ItemMeta meta = done.getItemMeta();
                    meta.setDisplayName(plugin.getConfig().getString("Captcha.nameSuccessItem",
                            "Not found String [Captcha.nameSuccessItem] in config.yml"));

                    done.setItemMeta(meta);

                    e.getInventory().setItem(e.getSlot(), done);
                    captchaModel.setCountDoneClick(captchaModel.getCountDoneClick() + 1);
                    captchaService.getMapActions().put(player.getName(), captchaModel);

                    if (captchaModel.getCountDoneClick() >= captchaService.getCountDone()) {
                        player.closeInventory();
                        captchaService.setTypeCaptcha(player, TypeCaptcha.NONE);
                        playerService.verify(player, false);
                        return;
                    }

                    if (captchaService.getTypeCaptcha(player) == TypeCaptcha.SHOW_ONE_ITEM_HIDE_DONE ||
                            captchaService.getTypeCaptcha(player) == TypeCaptcha.SHOW_ONE_ITEM) {
                        if (captchaService.getTypeCaptcha(player) == TypeCaptcha.SHOW_ONE_ITEM_HIDE_DONE) {
                            e.getInventory().setItem(e.getSlot(), null);
                        }
                        captchaService.nextShow(e);
                    }
                } else {
                    captchaModel.setCountMissClick(captchaModel.getCountMissClick() + 1);
                    captchaService.getMapActions().put(player.getName(), captchaModel);
                }

                if (captchaService.getMapActions().get(player.getName()).getCountMissClick() >
                        captchaService.getCountMiss()) {
                    printMessage.kickMessage(player, fileMessages.getMSG().getString("KickMessages.youBot",
                            "Not found string [KickMessages.youBot]"));
                    captchaService.getMapActions().remove(player.getName());
                }

                captchaService.getMapActions().put(player.getName(), captchaModel);
            } else {
                printMessage.kickMessage(player, fileMessages.getMSG().getString("KickMessages.youBot",
                        "Not found string [KickMessages.youBot]"));
                captchaService.getMapActions().remove(player.getName());
            }
        }
    }
}
