package me.towecraft.command;

import me.towecraft.TAS;
import me.towecraft.service.connect.ConnectionService;
import me.towecraft.service.connect.TypeConnect;
import me.towecraft.timers.LoginTimer;
import me.towecraft.utils.FileMessages;
import me.towecraft.utils.HashUtil;
import me.towecraft.utils.PluginLogger;
import me.towecraft.service.PrintMessageService;
import me.towecraft.utils.database.repository.PlayerAuthRepository;
import me.towecraft.utils.database.repository.PlayerRepository;
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
    private PlayerAuthRepository playerAuthRepository;

    @Autowire
    private ConnectionService connectionService;
    @Autowire
    private LoginTimer loginTimer;

    @Autowire
    private FileMessages fileMessages;
    @Autowire
    private PrintMessageService printMessageUtil;
    @Autowire
    private HashUtil hashUtil;
    @Autowire
    private PluginLogger logger;

    @PostConstruct
    private void init() {
        plugin.getCommand("l").setExecutor(this);
        plugin.getCommand("login").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (sender instanceof Player) {

                    playerRepository.findByUsername(sender.getName(), result -> {
                        if (result.isPresent()) {
                            if (args.length == 1) {
                                if (hashUtil.match(args[0], result.get().getPassword())) {
                                    printMessageUtil.sendMessage((Player) sender, fileMessages.getMSG().getString("AutoMessages.successLogin",
                                            "Not found string [AutoMessages.AutoMessages.successLogin]"));

                                    playerAuthRepository.saveLogin(result.get().getPlayerAuth()
                                            .setLastLogin(new Date())
                                            .setIpLogin(((Player) sender).getAddress().getHostName()), isLogin -> {
                                        if (isLogin) {
                                            loginTimer.removeTimer(sender.getName());
                                            connectionService.connect((Player) sender,
                                                    plugin.getConfig().getString("General.nextConnect", "Hub"),
                                                    TypeConnect.MIN);
                                        } else {
                                            logger.log("Error login");
                                        }
                                    });
                                } else
                                    printMessageUtil.sendMessage((Player) sender, fileMessages.getMSG().getString("AutoMessages.wrongPassword",
                                            "Not found string [AutoMessages.AutoMessages.wrongPassword]"));
                            } else
                                printMessageUtil.sendMessage((Player) sender, fileMessages.getMSG().getString("AutoMessages.wrongArgs",
                                        "Not found string [AutoMessages.AutoMessages.wrongArgs]"));
                        } else {
                            printMessageUtil.sendMessage((Player) sender, fileMessages.getMSG().getStringList("AutoMessages.register"));
                        }
                    });
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }
}
