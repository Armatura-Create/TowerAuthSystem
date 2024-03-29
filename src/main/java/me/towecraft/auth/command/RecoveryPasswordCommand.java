package me.towecraft.auth.command;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.repository.PlayerRepository;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.service.connect.ConnectionService;
import me.towecraft.auth.service.connect.TypeConnect;
import me.towecraft.auth.timers.RecoveryTimer;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.utils.HashUtil;
import me.towecraft.auth.utils.PluginLogger;
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
public class RecoveryPasswordCommand implements CommandExecutor {

    @Autowire
    private TAS plugin;

    @Autowire
    private PluginLogger logger;

    @Autowire
    private PrintMessageService printMessage;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private ConnectionService connectionService;

    @Autowire
    private HashUtil hashUtil;

    @Autowire
    private RecoveryTimer recoveryTimer;

    @PostConstruct
    private void init() {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)) {
            logger.log("Init command /password");
            plugin.getCommand("pass").setExecutor(this);
            plugin.getCommand("password").setExecutor(this);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (args.length == 2) {
                for (String s : plugin.getConfig().getStringList("Password.banned")) {
                    if (args[0].equals(s)) {
                        printMessage.sendMessage(player,
                                fileMessages.getMSG().getString("Commands.register.bannedPassword",
                                        "Not found [Commands.register.bannedPassword] in Message.yml"));
                        return true;
                    }
                }
                playerRepository.findByUsername(player.getName(), result -> result.ifPresent(p -> {
                    if (p.getPlayerAuth().getRecoveryCode().equals(args[0])) {
                        p.setPassword(hashUtil.toHash(args[1]));
                        playerRepository.savePassword(p);

                        p.getPlayerAuth()
                                .setRecoveryCode(null)
                                .setLastLogin(new Date());

                        playerRepository.save(p, isLogin -> {
                            if (isLogin) {
                                recoveryTimer.removeTimer(player);
                                printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.recovery.success",
                                        "Not found string [Commands.recovery.success] in Message.yml"));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        connectionService.connect(player,
                                                plugin.getConfig().getString("General.nextConnect", "Hub"),
                                                TypeConnect.MIN, 0);
                                    }
                                }.runTaskLater(plugin, 20L);
                            } else {
                                logger.log("Error login");
                                printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.error",
                                        "Not found string [Commands.error] in Message.yml"));
                            }
                        });
                    } else {
                        printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.recovery.wrongCode",
                                "Not found string [Commands.recovery.wrongCode] in Message.yml"));
                    }
                }));
            } else {
                printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.recovery.wrongArgs",
                        "Not found string [Commands.recovery.wrongArgs] in Message.yml"));
            }
        }
        return true;
    }
}
