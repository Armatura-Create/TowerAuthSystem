package me.towecraft.auth.service;

import me.towecraft.auth.TAS;
import me.towecraft.auth.listeners.captcha.CaptchaModel;
import me.towecraft.auth.listeners.captcha.TypeCaptcha;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    private Map<String, CaptchaModel> mapActions;
    private TypeCaptcha typeCaptcha;
    private Map<String, TypeCaptcha> currentTypeCaptchaMap;

    @PostConstruct
    public void init() {
        typeCaptcha = Arrays.stream(TypeCaptcha.values())
                .filter(t -> t.getType() == plugin.getConfig().getInt("General.captchaType", 0))
                .findFirst().orElse(TypeCaptcha.NONE);

        mapActions = new ConcurrentHashMap<>();
        currentTypeCaptchaMap = new ConcurrentHashMap<>();
    }

    public void showCaptcha(Player player) {
        if (player.isOnline()) {
            Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Проверка на бота");

            if (currentTypeCaptchaMap.get(player.getName()) == null) {
                if (typeCaptcha == TypeCaptcha.RANDOM) {
                    currentTypeCaptchaMap.put(player.getName(),
                            Arrays.stream(TypeCaptcha.values())
                                    .filter(t -> t.getType() == ((int) (Math.random() * 5) + 1))
                                    .findFirst().orElse(TypeCaptcha.SHOW_ALL_ITEM));
                } else {
                    currentTypeCaptchaMap.put(player.getName(), typeCaptcha);
                }
            }

            System.out.println(currentTypeCaptchaMap.get(player.getName()).name());

            if (currentTypeCaptchaMap.get(player.getName()) == TypeCaptcha.SHOW_ALL_ITEM) {
                List<Integer> random = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    int position = (int) (Math.random() * 54);
                    int finalPosition = position;
                    if (random.stream().anyMatch(s -> s == finalPosition))
                        position = (int) (Math.random() * 54);
                    random.add(position);
                }

                ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                ItemMeta meta = itemStackClick.getItemMeta();
                meta.setDisplayName("§cНажмите на меня");
                itemStackClick.setItemMeta(meta);
                for (Integer integer : random) {
                    inventory.setItem(integer, itemStackClick);
                }
            } else {
                ItemStack itemStackClick = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
                ItemMeta meta = itemStackClick.getItemMeta();
                meta.setDisplayName("§cНажмите на меня");
                itemStackClick.setItemMeta(meta);
                inventory.setItem((int) (Math.random() * 54), itemStackClick);
            }

            player.openInventory(inventory);
        }
    }

    public Map<String, CaptchaModel> getMapActions() {
        return mapActions;
    }

    public TypeCaptcha getTypeCaptcha(Player player) {
        return currentTypeCaptchaMap.get(player.getName());
    }

    public void removeTypeCaptcha(Player player) {
        currentTypeCaptchaMap.remove(player.getName());
    }

    public void setTypeCaptcha(Player player, TypeCaptcha typeCaptcha) {
        currentTypeCaptchaMap.put(player.getName(), typeCaptcha);
    }

}
