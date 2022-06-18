package me.towecraft.listeners.captcha;

import me.towecraft.TAS;
import me.towecraft.utils.FileMessages;
import me.towecraft.utils.PrintMessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class CaptchaListener implements Listener {

    @Autowire
    private TAS plugin;

    @Autowire
    private PrintMessageUtil printMessageUtil;

    @Autowire
    private FileMessages fileMessages;

    private HashMap<String, Integer> countMissClick;
    private HashMap<String, Integer> countDoneClick;

    private HashMap<String, Integer> fastClick;

    private HashMap<String, Boolean> isClick;

    private TypeCaptcha typeCaptcha;

    @PostConstruct
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

        typeCaptcha = Arrays.stream(TypeCaptcha.values())
                .filter(t -> t.getType() == plugin.getConfig().getInt("General.captchaType", 0))
                .findFirst().orElse(TypeCaptcha.NONE);

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

            if (typeCaptcha != TypeCaptcha.NONE) {
                showCaptcha((Player) e.getPlayer());
            }
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
                                        playerService.verify(player, false);
                                    }
                                }
                                if (typeCaptcha.getType() == 2 || typeCaptcha.getType() == 3) {
                                    if (typeCaptcha.getType() == 3) {
                                        e.getInventory().setItem(e.getSlot(), null);
                                    }
                                    ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                                    final ItemMeta metaClick = itemStackClick.getItemMeta();
                                    metaClick.setDisplayName("§cНажмите на меня");
                                    itemStackClick.setItemMeta(metaClick);
                                    e.getInventory().setItem(randomSlot(53), itemStackClick);
                                    player.updateInventory();
                                }
                            }
                        }
                        if (countMissClick.get(player.getName()) != null && countMissClick.get(player.getName()) > 3) {
                            printMessageUtil.kickMessage(player, fileMessages.getMSG().getString("KickMessages.IncorrectName"));
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
                        printMessageUtil.kickMessage(player, fileMessages.getMSG().getString("KickMessages.IncorrectBot"));
                        countMissClick.remove(player.getName());
                        fastClick.remove(player.getName());
                        isClick.remove(player.getName());
                    }
                }).start();
            } else {
                fastClick.merge(player.getName(), 1, Integer::sum);
                if (fastClick.get(player.getName()) > 10) {
                    printMessageUtil.kickMessage(player, fileMessages.getMSG().getString("KickMessages.IncorrectBot"));
                    countMissClick.remove(player.getName());
                    fastClick.remove(player.getName());
                    isClick.remove(player.getName());
                }

            }
        }
    }

    public void showCaptcha(Player player) {
        if (player.isOnline()) {
            Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Проверка на бота");

            if (typeCaptcha.getType() == 1) {
                List<Integer> random = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    int position = randomSlot(53);
                    int finalPosition = position;
                    if (random.stream().anyMatch(s -> s == finalPosition))
                        position = randomSlot(53);
                    random.add(position);
                }

                ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                final ItemMeta meta = itemStackClick.getItemMeta();
                meta.setDisplayName("§cНажмите на меня");
                itemStackClick.setItemMeta(meta);
                for (Integer integer : random) {
                    inventory.setItem(integer, itemStackClick);
                }
            } else {
                ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                final ItemMeta meta = itemStackClick.getItemMeta();
                meta.setDisplayName("§cНажмите на меня");
                itemStackClick.setItemMeta(meta);
                inventory.setItem(randomSlot(53), itemStackClick);
            }

            countDoneClick.put(player.getName(), 0);

            player.openInventory(inventory);

            captchaTimer.logTimer(player);
        }
    }

    private int randomSlot(int max) {
        return (int) (Math.random() * ++max);
    }

    public HashMap<String, Integer> getCountDoneClick() {
        return countDoneClick;
    }

    public HashMap<String, Integer> getCountMissClick() {
        return countMissClick;
    }
}
