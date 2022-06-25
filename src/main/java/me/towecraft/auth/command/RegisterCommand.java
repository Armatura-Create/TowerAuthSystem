package me.towecraft.auth.command;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.repository.PlayerAuthRepository;
import me.towecraft.auth.service.connect.ConnectionService;
import me.towecraft.auth.service.connect.TypeConnect;
import me.towecraft.auth.timers.RegisterTimer;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.utils.HashUtil;
import me.towecraft.auth.utils.PluginLogger;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.database.entity.PlayerAuthEntity;
import me.towecraft.auth.database.entity.PlayerEntity;
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

import static me.towecraft.auth.utils.MatcherUtil.checkEmail;

@Component
public class RegisterCommand implements CommandExecutor {

    @Autowire
    private TAS plugin;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private ConnectionService connectionService;

    @Autowire
    private RegisterTimer registerTimer;
    @Autowire
    private PrintMessageService printMessage;

    @Autowire
    private HashUtil hashUtil;
    @Autowire
    private PluginLogger logger;

    private int minPassLength;
    private int maxPassLength;

    private boolean requiredEmailRegister;

    @PostConstruct
    public void init() {
        plugin.getCommand("r").setExecutor(this);
        plugin.getCommand("reg").setExecutor(this);
        minPassLength = plugin.getConfig().getInt("Password.minLength", 6);
        maxPassLength = plugin.getConfig().getInt("Password.maxLength", 16);
        requiredEmailRegister = plugin.getConfig().getBoolean("SMTP.enable", false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = ((Player) sender).getPlayer();

            playerRepository.findByUsername(sender.getName(), result -> {
                if (result.isPresent()) {
                    printMessage.sendMessage(player,
                            fileMessages.getMSG().getString("Commands.register.existPlayer",
                                    "Not found [Commands.register.existPlayer] in Message.yml"));
                } else {
                    if (args.length < (requiredEmailRegister ? 3 : 2) || !args[0].equals(args[1])) {
                        printMessage.sendMessage(player, requiredEmailRegister ? fileMessages.getMSG().getString("Commands.register.wrongArgsWithEmail",
                                "Not found [Commands.register.wrongArgsWithEmail] in Message.yml") :
                                fileMessages.getMSG().getString("Commands.register.wrongArgs",
                                        "Not found [Commands.register.wrongArgs] in Message.yml"));
                        return;
                    }

                    for (String s : plugin.getConfig().getStringList("Password.banned")) {
                        if (args[0].equals(s)) {
                            printMessage.sendMessage(player,
                                    fileMessages.getMSG().getString("Commands.register.bannedPassword",
                                            "Not found [Commands.register.bannedPassword] in Message.yml"));
                            return;
                        }
                    }
                    if (args[0].length() < minPassLength || args[0].length() > maxPassLength) {
                        printMessage.sendMessage(player,
                                fileMessages.getMSG().getString("Commands.register.lengthPassword",
                                                "Not found [Commands.register.lengthPassword] in Message.yml")
                                        .replace("%min%", minPassLength + "")
                                        .replace("%max%", maxPassLength + ""));
                        return;
                    }

                    String email = null;

                    if (requiredEmailRegister)
                        if (!checkEmail(args[2])) {
                            printMessage.sendMessage(player,
                                    fileMessages.getMSG().getString("Commands.register.wrongEmail",
                                            "Not found [Commands.register.wrongEmail] in Message.yml"));
                            return;
                        } else
                            email = args[2];

                    PlayerAuthEntity playerAuth = new PlayerAuthEntity()
                            .setPlayerUuid(player.getUniqueId())
                            .setIpRegistration(player.getAddress().getHostName())
                            .setIpLogin(player.getAddress().getHostName())
                            .setTimeRegistration(new Date())
                            .setLastLogin(new Date());

                    PlayerEntity playerEntity = new PlayerEntity()
                            .setEmail(email)
                            .setPlayerAuth(playerAuth)
                            .setPassword(hashUtil.toHash(args[0]))
                            .setUsername(player.getName())
                            .setUuid(player.getUniqueId());

                    playerRepository.findByEmail(email, exist -> {
                        if (!exist) {
                            playerRepository.save(playerEntity, isReg -> {
                                if (isReg) {
                                    printMessage.sendMessage(player,
                                            fileMessages.getMSG().getString("Commands.register.successRegister",
                                                    "Not found [Commands.register.successRegister] in Message.yml"));
                                    registerTimer.removeTimer(player);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            connectionService.connect(player,
                                                    plugin.getConfig().getString("General.nextConnect", "Hub"),
                                                    TypeConnect.MIN, 0);
                                        }
                                    }.runTaskLater(plugin, 20L);

                                } else {
                                    logger.log("Error registration");
                                    printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.error",
                                            "Not found string [Commands.error] in Message.yml"));
                                }
                            });
                        } else {
                            printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.register.emailExist",
                                    "Not found string [Commands.register.emailExist] in Message.yml"));
                        }
                    });
                }
            });
        }
        return true;
    }
}