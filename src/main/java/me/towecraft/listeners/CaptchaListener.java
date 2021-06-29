package me.towecraft.listeners;

import me.towecraft.TAS;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class CaptchaListener implements Listener {

    private final HashMap<String, Integer> countMissClick;
    private final HashMap<String, Integer> countDoneClick;

    public CaptchaListener() {
        TAS.registerListener(this);
        countMissClick = new HashMap<>();
        countDoneClick = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (e.getPlayer() != null && e.getPlayer() instanceof Player && countDoneClick.get(e.getPlayer().getName()) != null && countDoneClick.get(e.getPlayer().getName()) < 3) {
            TAS.plugin.getPlayerMethods().sendVerifyMSG((Player) e.getPlayer(), true, true);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            final Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);

            if (e.getSlot() >= 0) {
                final ItemStack item = e.getInventory().getItem(e.getSlot());
                if (item == null)
                    countMissClick.merge(player.getName(), 1, Integer::sum);
                else {

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
                if (countMissClick.get(player.getName()) != null && countMissClick.get(player.getName()) > 3) {
                    player.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectName"));
                    countMissClick.remove(player.getName());
                }
            } else {
                player.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectBot"));
                countMissClick.remove(player.getName());
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
