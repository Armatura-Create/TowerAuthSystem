package me.towecraft.listeners;

import me.towecraft.TAS;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    public JoinListener() {
        TAS.registerListener(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PlayerJoinEvent e) {

        Player p = e.getPlayer();

        final String name = p.getName();

        if (name.matches("^[А-ЯЁа-яё]*")) {
            p.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectName"));
            return;
        }

        if (name.length() >= 3 && name.length() <= 16 && name.matches("^[a-zA-Z0-9_]*") && !name.contains("$") && !name.contains(" ") && !name.contains("-")) {
            TAS.captchaListener.getCountDoneClick().put(p.getName(), 0);
            TAS.plugin.getPlayerMethods().sendVerifyMSG(p, TAS.plugin.getPlayerDataList().searchPlayer(p.getName()) == null, true);
        } else {
            p.kickPlayer(TAS.files.getMSG().getString("KickMessages.IncorrectName"));
        }

        //TODO Проверка для сессии
    }
}
