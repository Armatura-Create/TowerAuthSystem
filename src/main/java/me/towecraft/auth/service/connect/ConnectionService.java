package me.towecraft.auth.service.connect;

import me.towecraft.auth.TAS;
import me.towecraft.auth.service.server.ServerModel;
import me.towecraft.auth.service.server.ServersUpdateHandler;
import me.towecraft.auth.service.server.TypeStatusServer;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.service.NameServerService;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.utils.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.PostConstruct;
import unsave.plugin.context.annotations.Service;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    @Autowire
    private TAS plugin;
    @Autowire
    private ServersUpdateHandler serversUpdateHandler;
    @Autowire
    private NameServerService nameServerService;
    @Autowire
    private FileMessages fileMessages;
    @Autowire
    private PluginLogger pluginLogger;
    @Autowire
    private PrintMessageService printMessage;

    private int countRetryReconnect;

    @PostConstruct
    private void init() {
        countRetryReconnect = plugin.getConfig().getInt("General.countRetryConnect", 10);
    }

    public void connect(Player player, String pieceTypeServer, TypeConnect typeConnect, int nowReconnect) {

        List<ServerModel> servers = new ArrayList<>(serversUpdateHandler.getServers());

        servers = servers
                .stream()
                .filter(s -> s.getStatus() == TypeStatusServer.ONLINE)
                .filter(s -> s.getName().contains(pieceTypeServer))
                .sorted(Comparator.comparing(ServerModel::getNowPlayer))
                .collect(Collectors.toList());

        if (servers.size() < 1) {
            printMessage.sendMessage(player, fileMessages.getMSG().getString("Connect.tryReconnect",
                            "String not found [Connect.tryReconnect] in Message.yml")
                    .replace("%now%", nowReconnect + "")
                    .replace("%all%", countRetryReconnect + "")
            );

            if (nowReconnect < countRetryReconnect)
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        connect(player, pieceTypeServer, typeConnect, nowReconnect + 1);
                    }
                }.runTaskLater(plugin, 10_000);
            else {
                printMessage.sendMessage(player, fileMessages.getMSG().getString("Connect.notFoundOnlineNextServer",
                        "String not found [Connect.notFoundOnlineNextServer] in Message.yml"));
            }
        }

        switch (typeConnect) {
            case RANDOM:
                Collections.shuffle(servers);
                break;

            case MAX:
                Collections.reverse(servers);
                break;
        }

        if (servers.size() > 0) {
            if (nameServerService.getNameServer().equalsIgnoreCase(servers.get(0).getName())) {
                player.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&',
                        fileMessages.getMSG().getString("Connect.areYouHere",
                                "String not found [Connect.areYouHere] in Message.yml")) + "§a" + pieceTypeServer);
                return;
            }

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(servers.get(0).getName());
            } catch (IOException e) {
                pluginLogger.log(e.getMessage());
            }

            player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        }
    }
}
