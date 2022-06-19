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
        if (e.getPlayer() != null && e.getPlayer() instanceof Player &&
                captchaService.getMapActions().get(e.getPlayer().getName()).getCountDoneClick() < 3) {

            if (captchaService.getTypeCaptcha() != TypeCaptcha.NONE) {
                captchaService.showCaptcha((Player) e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            CaptchaModel captchaModel = captchaService.getMapActions().get(player.getName());

            if (!captchaModel.isClick()) {
                captchaModel.setClick(true);
                captchaService.getMapActions().put(player.getName(), captchaModel);

                new Thread(() -> {
                    if (e.getSlot() >= 0) {
                        final ItemStack item = e.getInventory().getItem(e.getSlot());

                        if (item == null) {
                            captchaModel.setCountMissClick(captchaModel.getCountMissClick() + 1);
                            captchaService.getMapActions().put(player.getName(), captchaModel);
                        } else {
                            if (!e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equals("§aВыполнено")) {
                                ItemStack done = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
                                final ItemMeta meta = done.getItemMeta();
                                meta.setDisplayName("§aВыполнено");

                                done.setItemMeta(meta);
                                if (!done.equals(e.getInventory().getItem(e.getSlot()))) {
                                    e.getInventory().setItem(e.getSlot(), done);
                                    captchaModel.setCountDoneClick(captchaModel.getCountDoneClick() + 1);
                                    captchaService.getMapActions().put(player.getName(), captchaModel);
                                    if (captchaModel.getCountDoneClick() >= 3) {
                                        player.closeInventory();
                                        playerService.verify(player, false);
                                    }
                                }
                                if (captchaService.getTypeCaptcha().getType() == 2 ||
                                        captchaService.getTypeCaptcha().getType() == 3) {
                                    if (captchaService.getTypeCaptcha().getType() == 3) {
                                        e.getInventory().setItem(e.getSlot(), null);
                                    }
                                    ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                                    final ItemMeta metaClick = itemStackClick.getItemMeta();
                                    metaClick.setDisplayName("§cНажмите на меня");
                                    itemStackClick.setItemMeta(metaClick);
                                    e.getInventory().setItem((int) (Math.random() * 54), itemStackClick);
                                    player.updateInventory();
                                }
                            }
                        }
                        if (captchaModel.getCountMissClick() > 3) {
                            printMessage.kickMessage(player, fileMessages.getMSG().getString("KickMessages.youBot",
                                    "Not found string [KickMessages.youBot]"));
                            captchaService.getMapActions().remove(player.getName());
                        }
                        try {
                            Thread.sleep(120);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        captchaModel.setFastClick(0);
                        captchaModel.setClick(false);
                        captchaService.getMapActions().put(player.getName(), captchaModel);
                    } else {
                        printMessage.kickMessage(player, fileMessages.getMSG().getString("KickMessages.youBot",
                                "Not found string [KickMessages.youBot]"));
                        captchaService.getMapActions().remove(player.getName());
                    }
                }).start();
            } else {
                captchaModel.setFastClick(captchaModel.getFastClick() + 1);
                if (captchaModel.getFastClick() > 10) {
                    printMessage.kickMessage(player, fileMessages.getMSG().getString("KickMessages.youBot",
                            "Not found string [KickMessages.youBot]"));
                    captchaService.getMapActions().remove(player.getName());
                }
            }
        }
    }
}
