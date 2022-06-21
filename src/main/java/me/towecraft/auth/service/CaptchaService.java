package me.towecraft.auth.service;

import me.towecraft.auth.TAS;
import me.towecraft.auth.listeners.captcha.CaptchaModel;
import me.towecraft.auth.listeners.captcha.TypeCaptcha;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CaptchaService {

    @Autowire
    private TAS plugin;
    private Map<Player, CaptchaModel> mapActions;
    private TypeCaptcha typeCaptcha;
    private Map<Player, TypeCaptcha> currentTypeCaptchaMap;

    private int countDone;
    private int countMiss;

    @PostConstruct
    public void init() {
        typeCaptcha = Arrays.stream(TypeCaptcha.values())
                .filter(t -> t.getType() == plugin.getConfig().getInt("Captcha.type", 0))
                .findFirst().orElse(TypeCaptcha.NONE);

        mapActions = new ConcurrentHashMap<>();
        currentTypeCaptchaMap = new ConcurrentHashMap<>();
        countDone = plugin.getConfig().getInt("Captcha.countCaptcha", 3);

        if (countDone > 54)
            countDone = 54;

        countMiss = plugin.getConfig().getInt("Captcha.countMissClick", 3);
    }

    public void showCaptcha(Player player) {
        if (player.isOnline()) {
            Inventory inventory = Bukkit.createInventory(null, 6 * 9,
                    plugin.getConfig().getString("Captcha.nameCaptcha",
                            "Not found String [Captcha.nameCaptcha] in config.yml"));

            removeTypeCaptcha(player);
            if (currentTypeCaptchaMap.get(player) == null) {
                if (typeCaptcha == TypeCaptcha.RANDOM) {
                    currentTypeCaptchaMap.put(player,
                            Arrays.stream(TypeCaptcha.values())
                                    .filter(t -> t.getType() == ((int) (Math.random() * 3) + 1))
                                    .findFirst().orElse(TypeCaptcha.SHOW_ALL_ITEM));
                } else {
                    currentTypeCaptchaMap.put(player, typeCaptcha);
                }
            }

            mapActions.put(player, new CaptchaModel());

            ItemStack itemStackClick = new ItemStack(Material.REDSTONE_BLOCK, 1);
            ItemMeta meta = itemStackClick.getItemMeta();
            meta.setDisplayName(plugin.getConfig().getString("Captcha.nameItem",
                    "Not found String [Captcha.nameItem] in config.yml"));
            itemStackClick.setItemMeta(meta);

            if (currentTypeCaptchaMap.get(player) == TypeCaptcha.SHOW_ALL_ITEM) {
                for (Integer integer : generatePositions(new ArrayList<>())) {
                    inventory.setItem(integer, itemStackClick);
                }
            } else
                inventory.setItem((int) (Math.random() * 54), itemStackClick);

            player.openInventory(inventory);
        }
    }

    private List<Integer> generatePositions(List<Integer> result) {

        int position = (int) (Math.random() * 54);

        if (result.stream().anyMatch(p -> p == position))
            result = generatePositions(result);
        else
            result.add(position);

        if (result.size() < countDone)
            result = generatePositions(result);

        return result;
    }

    public void nextShow(InventoryClickEvent e) {
        int position = (int) (Math.random() * 54);

        if (e.getInventory().getItem(position) != null)
            nextShow(e);

        ItemStack itemStackClick = new ItemStack(Material.REDSTONE_BLOCK, 1);
        ItemMeta metaClick = itemStackClick.getItemMeta();
        metaClick.setDisplayName(plugin.getConfig().getString("Captcha.nameItem",
                "Not found String [Captcha.nameItem] in config.yml"));
        itemStackClick.setItemMeta(metaClick);

        e.getInventory().setItem(position, itemStackClick);
        ((Player) e.getWhoClicked()).updateInventory();
    }

    public Map<Player, CaptchaModel> getMapActions() {
        return mapActions;
    }

    public TypeCaptcha getTypeCaptcha(Player player) {
        return currentTypeCaptchaMap.get(player);
    }

    public void removeTypeCaptcha(Player player) {
        currentTypeCaptchaMap.remove(player);
    }

    public void setTypeCaptcha(Player player, TypeCaptcha typeCaptcha) {
        currentTypeCaptchaMap.put(player, typeCaptcha);
    }

    public int getCountDone() {
        return countDone;
    }

    public int getCountMiss() {
        return countMiss;
    }

    public synchronized void incrementDone(Player player) {
        CaptchaModel captchaModel = mapActions.get(player);
        if (captchaModel == null)
            mapActions.put(player, new CaptchaModel().setCountDoneClick(1));
        else {
            captchaModel.setCountDoneClick(captchaModel.getCountDoneClick() + 1);
            mapActions.put(player, captchaModel);
        }
    }

    public synchronized void incrementMiss(Player player) {
        CaptchaModel captchaModel = mapActions.get(player);
        if (captchaModel == null)
            mapActions.put(player, new CaptchaModel().setCountMissClick(1));
        else {
            captchaModel.setCountDoneClick(captchaModel.getCountMissClick() + 1);
            mapActions.put(player, captchaModel);
        }
    }
}
