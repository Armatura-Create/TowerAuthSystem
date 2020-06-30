package me.towecraft.utils;

import me.towecraft.TAS;
import me.towecraft.utils.models.cache.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PlayerMethods {

    private final TAS plugin;

    public PlayerMethods(TAS plugin) {
        this.plugin = plugin;
    }

    public void sendVerifyMSG(Player player, boolean isCaptcha, boolean isCheckIp) {
        if (isCaptcha && TAS.captchaListener.getCountDoneClick().get(player.getName()) < 3 ) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    showCaptcha(player);
                }
            }.runTaskLater(plugin, 5L);
            return;
        } else if (isCheckIp && TAS.captchaListener.getCountDoneClick().get(player.getName()) < 3) {
            PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());
            if (!playerData.getLog_ip().equals(player.getAddress().getAddress().getHostAddress())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        showCaptcha(player);
                    }
                }.runTaskLater(plugin, 5L);
                return;
            }
        }
        if (player.isOnline()) {
            PlayerData playerData = plugin.getPlayerDataList().searchPlayer(player.getName());
            if (playerData != null) {
                if (playerData.getLog_ip().equals(player.getAddress().getAddress().getHostAddress()) &&
                        playerData.getLast_login().getTime() >= new Timestamp(System.currentTimeMillis()).getTime() - TAS.maxTimeSession) {
                    pMessage(player, 10);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            TAS.connect(player, "Hub_min");
                        }
                    }.runTaskLater(plugin, 20L);

                } else {
                    pMessage(player, 2);
                    plugin.getLoginTimer().logTimer(player);
                }
            } else {
                pMessage(player, 3);
                plugin.getRegisterTimer().regTimer(player);
            }
        }
    }

    private void showCaptcha(Player player) {
        if (player.isOnline()) {
            Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Проверка на бота");

            if (TAS.plugin.getTypeCaptcha() == 1) {
                List<Integer> random = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    int position = rnd(0, 53);
                    int finalPosition = position;
                    if (random.stream().anyMatch(s -> s == finalPosition))
                        position = rnd(position + 1, 53);
                    random.add(position);
                }

                ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                final ItemMeta meta = itemStackClick.getItemMeta();
                meta.setDisplayName("§cНажмите на меня");
                itemStackClick.setItemMeta(meta);
                for (Integer integer : random) {
                    inventory.setItem(integer, itemStackClick);
                }
            } else  {
                ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                final ItemMeta meta = itemStackClick.getItemMeta();
                meta.setDisplayName("§cНажмите на меня");
                itemStackClick.setItemMeta(meta);
                inventory.setItem(rnd(0, 53), itemStackClick);
            }

            TAS.captchaListener.getCountDoneClick().put(player.getName(), 0);

            player.openInventory(inventory);

            TAS.getCaptchaTimer().logTimer(player);
        }
    }

    public int rnd(int min, int max) {
        max -= min;
        return (int) (Math.random() * ++max) + min;
    }

    public void pMessage(final Player player, final int n) {
        List<String> listMSG = new ArrayList<>();
        switch (n) {
            case 2:
                listMSG = TAS.files.getMSG().getStringList("AutoMessages.login");
                break;

            case 3:
                listMSG = TAS.files.getMSG().getStringList("AutoMessages.register");
                break;

            case 10:
                listMSG = TAS.files.getMSG().getStringList("AutoMessages.session");
                break;
        }

        for (String temp : listMSG)
            player.sendMessage(temp);
    }
}
