package me.towecraft;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.towecraft.listeners.CaptchaListener;
import me.towecraft.listeners.JoinListener;
import me.towecraft.utils.Files;
import me.towecraft.utils.PlayerMethods;
import me.towecraft.utils.SpigotUpdater;
import me.towecraft.utils.cmd.LoginCMD;
import me.towecraft.utils.cmd.RegisterCMD;
import me.towecraft.utils.models.ServerModel;
import me.towecraft.utils.models.cache.PlayerDataList;
import me.towecraft.utils.mysql.MySQL;
import me.towecraft.utils.timers.CaptchaTimer;
import me.towecraft.utils.timers.LoginTimer;
import me.towecraft.utils.timers.RegisterTimer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import static me.towecraft.utils.mysql.PoolManager.connectDB;

public final class TAS extends JavaPlugin implements CommandExecutor, PluginMessageListener {

    public static Files files;
    public static long maxTimeSession = 1800_000L; //30 minute
    private PlayerDataList playerDataList;
    private int typeCaptcha = 3;

    public long updateTime;

    private RegisterTimer registerTimer;
    private LoginTimer loginTimer;
    private static CaptchaTimer captchaTimer;

    public static CaptchaListener captchaListener;

    public static String nameServer = null;
    public static List<ServerModel> servers;
    public static TAS plugin;

    private PlayerMethods playerMethods;

    Thread updater;
    Runnable mainRunnable;

    @Override
    public void onEnable() {

//        SpigotUpdater updater = new SpigotUpdater(this, 76667);
//        try {
//            if (updater.checkForUpdates())
//                log("An update was found! New version: " + updater.getLatestVersion() + " download: " + updater.getResourceURL());
//        } catch (Exception e) {
//            log("Could not check for updates! Stacktrace:");
//            e.printStackTrace();
//        }

        log("Start load");

        this.saveDefaultConfig();

        plugin = this;
        playerMethods = new PlayerMethods(this);
        typeCaptcha = this.getConfig().getInt("General.captchaType");

        servers = new ArrayList<>();

        loginTimer = new LoginTimer();
        registerTimer = new RegisterTimer();
        captchaTimer = new CaptchaTimer();

        this.getCommand("l").setExecutor(new LoginCMD());
        this.getCommand("login").setExecutor(new LoginCMD());
        this.getCommand("reg").setExecutor(new RegisterCMD());
        this.getCommand("register").setExecutor(new RegisterCMD());

        playerDataList = new PlayerDataList(this);

        this.getCommand("connect").setExecutor(this);

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        //Регистрируем канал для получения серваков
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "tgs:channel");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "tgs:channel", this);

        updateTime = this.getConfig().getLong("General.updateInterval");

        this.staticRunnable();
        this.startUpdate();

        //Инициализируем файл с сообщениями
        files = new Files(this);
        files.createMessages();

        loadListeners();

        connectDB();
        MySQL.getPlayers(null);
    }

    private void loadListeners() {
        new JoinListener();
        captchaListener = new CaptchaListener();
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "tgs:channel");
        log("Stop");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String commandLabel, final String[] args) {
        if ("connect".equals(commandLabel.split("_")[0])) {
            if (sender instanceof Player)
                if (args.length > 0)
                    connect((Player) sender, args[0] + "_min");
                else
                    sender.sendMessage(getPrefix() + "Не задан сервер");
            return true;
        }
        sender.sendMessage(getPrefix() + "Команда не найдена");
        return true;
    }

    public static void connect(final Player p, String where) {

        String type = where.split("_")[1];

        List<ServerModel> servers = new ArrayList<>();

        TAS.servers.sort(Comparator.comparing(ServerModel::getNowPlayer));

        for (final ServerModel s : TAS.servers)
            if (s.getName().contains(where.split("_")[0]))
                servers.add(s);

        if (servers.size() < 1) {
            String result;
            result = "Не найден сервер";
            p.sendMessage(getPrefix() + result);
        }

        switch (type) {
            case "random":
                Collections.shuffle(servers);
                break;

            case "max":
                Collections.reverse(servers);
                break;
        }

        if (servers.size() > 0) {

            if (nameServer.equalsIgnoreCase(servers.get(0).getName())) {
                String result = "Вы уже находитесь в - ";
                p.sendMessage(getPrefix() + result + "§a" + nameServer);
                return;
            }

            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(servers.get(0).getName());
            } catch (IOException e) {
                log(e.getMessage());
            }

            p.sendPluginMessage(TAS.plugin, "BungeeCord", b.toByteArray());
        }
    }

    //-----------------------------CHECK_SERVERS--------------------------------//

    public void startUpdate() {
        (this.updater = new Thread(() -> {
            while (true) {
                try {
                    while (true) {
                        Thread.sleep(updateTime); // Update UI in two seconds

                        if (Bukkit.getOnlinePlayers().toArray().length > 0 && nameServer == null)
                            setCurrentServer();

                        if (nameServer != null)
                            TAS.this.mainRunnable.run();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        })).start();
    }

    void staticRunnable() {
        this.mainRunnable = () -> {
            final ByteArrayOutputStream b = new ByteArrayOutputStream();
            final DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF(nameServer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            getServer().sendPluginMessage(plugin, "tgs:channel", b.toByteArray());
        };
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("tgs:channel")) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        servers.clear();

        while (true) {
            try {
                String[] data = in.readUTF().split(":");

                if (data[0].equals("serverName")) {
                    nameServer = data[1];
                    return;
                }

                ServerModel temp = new ServerModel(data[1], data[2], data[3], data[4], Integer.parseInt(data[5]), Integer.parseInt(data[6]));

                if (data[0].equals("server") && temp.getInStatus().equals("online"))
                    servers.add(temp);

            } catch (Exception e) {
                break;
            }
        }
    }

    public void setCurrentServer() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        Player player = (Player) Bukkit.getOnlinePlayers().toArray()[0];
        out.writeUTF(player.getName());
        player.sendPluginMessage(this, "tgs:channel", out.toByteArray());
    }

    //-----------------------------CHECK_SERVERS--------------------------------//

    // STATIC //

    public static void log(String message) {
        Logger.getLogger("Minecraft").info("[TowerAuthSystem] " + message);
    }

    public static String getPrefix() {
        return "§6TAS §8» §7";
    }

    public static void registerListener(final Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, TAS.plugin);
    }

    // STATIC //

    public PlayerDataList getPlayerDataList() {
        return playerDataList;
    }

    public RegisterTimer getRegisterTimer() {
        return registerTimer;
    }

    public LoginTimer getLoginTimer() {
        return loginTimer;
    }

    public static CaptchaTimer  getCaptchaTimer() {
        return captchaTimer;
    }

    public PlayerMethods getPlayerMethods() {
        return playerMethods;
    }

    public void setPlayerMethods(PlayerMethods playerMethods) {
        this.playerMethods = playerMethods;
    }

    public int getTypeCaptcha() {
        return typeCaptcha;
    }
}
