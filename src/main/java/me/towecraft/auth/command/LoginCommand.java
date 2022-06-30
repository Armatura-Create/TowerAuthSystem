package me.towecraft.auth.command;

import me.towecraft.auth.TAS;
import me.towecraft.auth.service.CaptchaService;
import me.towecraft.auth.service.connect.ConnectionService;
import me.towecraft.auth.service.connect.TypeConnect;
import me.towecraft.auth.timers.LoginTimer;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.utils.HashUtil;
import me.towecraft.auth.utils.PluginLogger;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.database.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import java.util.Date;


@Component
public class LoginCommand implements CommandExecutor {

    @Autowire
    private TAS plugin;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private ConnectionService connectionService;
    @Autowire
    private CaptchaService captchaService;
    @Autowire
    private LoginTimer loginTimer;

    @Autowire
    private FileMessages fileMessages;
    @Autowire
    private PrintMessageService printMessage;
    @Autowire
    private HashUtil hashUtil;
    @Autowire
    private PluginLogger logger;

    private String nextConnect;

    @PostConstruct
    private void init() {
        plugin.getCommand("l").setExecutor(this);
        plugin.getCommand("login").setExecutor(this);

        nextConnect = plugin.getConfig().getString("General.nextConnect");

        if (nextConnect == null)
            throw new RuntimeException("Not found next connect server [General.nextConnect] in config.yml");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {
                    Player player = ((Player) sender).getPlayer();

                    playerRepository.findByUsername(player.getName(), result -> {
                        if (result.isPresent()) {
                            if (args.length == 1) {
                                if (hashUtil.match(args[0], result.get().getPassword())) {
                                    printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.login.successLogin",
                                            "Not found string [Commands.login.successLogin]"));

                                    result.get().getPlayerAuth()
                                            .setLastLogin(new Date())
                                            .setIpLogin(player.getAddress().getHostName());

                                    playerRepository.save(result.get(), isLogin -> {
                                        if (isLogin) {
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    loginTimer.removeTimer(player);
                                                    connectionService.connect(player,
                                                            nextConnect,
                                                            TypeConnect.MIN, 0);
                                                    captchaService.removeTypeCaptcha(player);
                                                }
                                            }.runTaskLater(plugin, 20L);
                                        } else {
                                            logger.log("Error login");
                                            printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.error",
                                                    "Not found string [Commands.error] in Message.yml"));
                                        }
                                    });
                                } else
                                    printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.login.wrongPassword",
                                            "Not found string [Commands.login.wrongPassword]"));
                            } else
                                printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.login.wrongArgs",
                                        "Not found string [Commands.login.wrongArgs]"));
                        } else {
                            printMessage.sendMessage(player, fileMessages.getMSG().getStringList("Commands.register.wrongArgs"));
                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
