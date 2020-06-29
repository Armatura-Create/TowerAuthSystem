package me.towecraft.utils.models.cache;

import me.towecraft.TAS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerDataList {
    public TAS plugin;
    private final HashMap<String, PlayerData> player;

    public PlayerDataList(final TAS plugin) {
        this.plugin = plugin;
        this.player = new HashMap<>();
    }

    public boolean addPlayer(final PlayerData playerData) {
        if (this.player.get(playerData.getName()) == null) {
            this.player.put(playerData.getName(), playerData);
            return true;
        }
        return false;
    }

    public PlayerData searchPlayer(String s) {
        return this.player.get(s);
    }

    public boolean modifyPlayer(final PlayerData playerData) {
        final PlayerData searchPlayer = this.searchPlayer(playerData.getName());
        if (searchPlayer != null) {
            this.player.replace(playerData.getName(), searchPlayer, playerData);
            return true;
        } else
            this.player.put(playerData.getName(), playerData);
        return false;
    }

    public boolean removePlayer(final String s) {
        if (this.searchPlayer(s) != null) {
            this.player.remove(s);
            return true;
        }
        return false;
    }

//    public void cleanData() {
//        plugin.getProxy().getScheduler().schedule(plugin, () -> {
//            HashSet<String> set = new HashSet<>();
//            for (Map.Entry<String, PlayerData> entry : this.player.entrySet()) {
//                if (!entry.getValue().isPlaying()) {
//                    set.add(entry.getKey());
//                }
//            }
//            this.player.keySet().removeAll(set);
//        }, 1L, 30, TimeUnit.MINUTES);
//    }
}
