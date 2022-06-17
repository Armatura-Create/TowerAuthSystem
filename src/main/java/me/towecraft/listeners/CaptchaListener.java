package me.towecraft.listeners;

import me.towecraft.TAS;
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

import java.util.HashMap;

@Component
public class CaptchaListener implements Listener {

    @Autowire
    private TAS plugin;

    private HashMap<String, Integer> countMissClick;
    private HashMap<String, Integer> countDoneClick;

    private HashMap<String, Integer> fastClick;

    private HashMap<String, Boolean> isClick;

    @PostConstruct
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        countMissClick = new HashMap<>();
        countDoneClick = new HashMap<>();
        fastClick = new HashMap<>();
        isClick = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (e.getPlayer() != null && e.getPlayer() instanceof Player &&
                countDoneClick.get(e.getPlayer().getName()) != null &&
                countDoneClick.get(e.getPlayer().getName()) < 3) {
            TAS.plugin.getPlayerMethods().sendVerifyMSG((Player) e.getPlayer(), true, true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            isClick.putIfAbsent(player.getName(), false);

            if (!isClick.get(player.getName())) {
                isClick.put(player.getName(), true);
                new Thread(() -> {
                    if (e.getSlot() >= 0) {
                        final ItemStack item = e.getInventory().getItem(e.getSlot());

                        if (item == null) {
                            countMissClick.merge(player.getName(), 1, Integer::sum);
                        } else {
                            if (!e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName().equals("§aВыполнено")) {
                                ItemStack done = new ItemStack(Material.STAINED_CLAY, 1, (byte) 5);
                                final ItemMeta meta = done.getItemMeta();
                                meta.setDisplayName("§aВыполнено");
                                done.setItemMeta(meta);
                                if (!done.equals(e.getInventory().getItem(e.getSlot()))) {
                                    e.getInventory().setItem(e.getSlot(), done);
                                    countDoneClick.merge(player.getName(), 1, Integer::sum);
                                    if (countDoneClick.get(player.getName()) >= 3) {
                                        player.closeInventory();
                                        TAS.plugin.getPlayerMethods().sendVerifyMSG(player, false, false);
                                    }
                                }
                                if (TAS.plugin.getTypeCaptcha() == 2 || TAS.plugin.getTypeCaptcha() == 3) {
                                    if (TAS.plugin.getTypeCaptcha() == 3) {
                                        e.getInventory().setItem(e.getSlot(), null);
                                    }
                                    ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                                    final ItemMeta metaClick = itemStackClick.getItemMeta();
                                    metaClick.setDisplayName("§cНажмите на меня");
                                    itemStackClick.setItemMeta(metaClick);
                                    e.getInventory().setItem(TAS.plugin.getPlayerMethods().rnd(0, 53), itemStackClick);
                                    player.updateInventory();
                                }
                            }
                        }
                        if (countMissClick.get(player.getName()) != null && countMissClick.get(player.getName()) > 3) {
                            player.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectName"));
                            countMissClick.remove(player.getName());
                            fastClick.remove(player.getName());
                            isClick.remove(player.getName());
                        }
                        try {
                            Thread.sleep(120);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        fastClick.put(player.getName(), 0);
                        isClick.put(player.getName(), false);
                    } else {
                        player.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectBot"));
                        countMissClick.remove(player.getName());
                        fastClick.remove(player.getName());
                        isClick.remove(player.getName());
                    }
                }).start();
            } else {
                fastClick.merge(player.getName(), 1, Integer::sum);
                if (fastClick.get(player.getName()) > 10) {
                    player.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectBot"));
                    countMissClick.remove(player.getName());
                    fastClick.remove(player.getName());
                    isClick.remove(player.getName());
                }

            }
        }
    }

    public HashMap<String, Integer> getCountDoneClick() {
        return countDoneClick;
    }

    public HashMap<String, Integer> getCountMissClick() {
        return countMissClick;
    }
}
