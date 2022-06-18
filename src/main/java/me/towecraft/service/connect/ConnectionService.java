package me.towecraft.service.connect;

import me.towecraft.TAS;
import me.towecraft.service.server.ServerModel;
import me.towecraft.service.server.ServersUpdateHandler;
import me.towecraft.service.server.TypeStatusServer;
import me.towecraft.utils.FileMessages;
import me.towecraft.utils.NameServerService;
import me.towecraft.utils.PrintMessageUtil;
import me.towecraft.utils.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import unsave.plugin.context.annotations.Autowire;
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
    private PrintMessageUtil printMessageUtil;

    public void connect(Player player, String pieceTypeServer, TypeConnect typeConnect) {

        List<ServerModel> servers = new ArrayList<>(serversUpdateHandler.getServers());

        servers = servers
                .stream()
                .filter(s -> s.getStatus() == TypeStatusServer.ONLINE)
                .filter(s -> s.getName().contains(pieceTypeServer))
                .sorted(Comparator.comparing(ServerModel::getNowPlayer))
                .collect(Collectors.toList());

        if (servers.size() < 1)
            printMessageUtil.sendMessage(player, fileMessages.getMSG().getString("GUI.main.wrongArgumentConnect",
                    "String not found (GUI.main.wrongArgumentConnect)"));

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
                        fileMessages.getMSG().getString("GUI.main.areYouHere", "String not found (GUI.main.areYouHere)")) + "§a" + pieceTypeServer);
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