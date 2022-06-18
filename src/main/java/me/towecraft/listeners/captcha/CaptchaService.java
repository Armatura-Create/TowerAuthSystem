package me.towecraft.listeners.captcha;

import me.towecraft.TAS;
import me.towecraft.service.PlayerService;
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
    @Autowire
    private PlayerService playerService;
    private Map<String, CaptchaModel> mapActions;
    private TypeCaptcha typeCaptcha;

    @PostConstruct
    public void init() {
        typeCaptcha = Arrays.stream(TypeCaptcha.values())
                .filter(t -> t.getType() == plugin.getConfig().getInt("General.captchaType", 0))
                .findFirst().orElse(TypeCaptcha.NONE);

        mapActions = new ConcurrentHashMap<>();
    }

    public void success(Player player) {
        playerService.verify(player, false);
    }

    public void showCaptcha(Player player) {
        if (player.isOnline()) {
            Inventory inventory = Bukkit.createInventory(null, 6 * 9, "Проверка на бота");

            if (typeCaptcha.getType() == 1) {
                List<Integer> random = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    int position = (int) (Math.random() * 54);
                    int finalPosition = position;
                    if (random.stream().anyMatch(s -> s == finalPosition))
                        position = (int) (Math.random() * 54);
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
                inventory.setItem((int) (Math.random() * 54), itemStackClick);
            }

            mapActions.put(player.getName(), new CaptchaModel());

            player.openInventory(inventory);
        }
    }

    public Map<String, CaptchaModel> getMapActions() {
        return mapActions;
    }

    public TypeCaptcha getTypeCaptcha() {
        return typeCaptcha;
    }

}
