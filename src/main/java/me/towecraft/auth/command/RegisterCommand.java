package me.towecraft.auth.command;

import me.towecraft.auth.TAS;
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

    @PostConstruct
    public void init() {
        plugin.getCommand("r").setExecutor(this);
        plugin.getCommand("reg").setExecutor(this);
        minPassLength = plugin.getConfig().getInt("Password.minLength", 6);
        maxPassLength = plugin.getConfig().getInt("Password.maxLength", 16);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            playerRepository.findByUsername(sender.getName(), result -> {
                if (result.isPresent()) {
                    printMessage.sendMessage((Player) sender,
                            fileMessages.getMSG().getString("Commands.register.existPlayer",
                                    "Not found [Commands.register.existPlayer] in Message.yml"));
                } else {
                    if (args.length < 2 || !args[0].equals(args[1])) {
                        printMessage.sendMessage((Player) sender,
                                fileMessages.getMSG().getString("Commands.register.wrongArgs",
                                        "Not found [Commands.register.wrongArgs] in Message.yml"));
                        return;
                    }
//                    if (checkRusSymbol(args[1]) || checkContainsRusSymbol(args[1])) {
//                        printMessage.sendMessage((Player) sender,
//                                fileMessages.getMSG().getString("Commands.register.cyrillic_pass",
//                                        "Not found [Commands.register.cyrillic_pass] in Message.yml"));
//                        return;
//                    }

                    for (String s : plugin.getConfig().getStringList("Password.banned")) {
                        if (args[0].equals(s)) {
                            printMessage.sendMessage((Player) sender,
                                    fileMessages.getMSG().getString("Commands.register.bannedPassword",
                                            "Not found [Commands.register.bannedPassword] in Message.yml"));
                            return;
                        }
                    }
                    if (args[0].length() < minPassLength || args[0].length() > maxPassLength) {
                        printMessage.sendMessage((Player) sender,
                                fileMessages.getMSG().getString("Commands.register.lengthPassword",
                                                "Not found [Commands.register.lengthPassword] in Message.yml")
                                        .replace("%min%", minPassLength + "")
                                        .replace("%max%", maxPassLength + ""));
                        return;
                    }

                    String email = null;

                    if (args.length == 3) {
                        if (!checkEmail(args[2])) {
                            printMessage.sendMessage((Player) sender,
                                    fileMessages.getMSG().getString("Commands.register.wrongEmail",
                                            "Not found [Commands.register.wrongEmail] in Message.yml"));
                            return;
                        } else
                            email = args[2];
                    }

                    PlayerAuthEntity playerAuth = new PlayerAuthEntity()
                            .setPlayerUuid(((Player) sender).getUniqueId())
                            .setIpRegistration(((Player) sender).getAddress().getHostName())
                            .setIpLogin(((Player) sender).getAddress().getHostName())
                            .setTimeRegistration(new Date())
                            .setLastLogin(new Date());

                    PlayerEntity player = new PlayerEntity()
                            .setEmail(email)
                            .setPlayerAuth(playerAuth)
                            .setPassword(hashUtil.toHash(args[0]))
                            .setUsername(sender.getName())
                            .setUuid(((Player) sender).getUniqueId());

                    playerRepository.save(player, isReg -> {
                        if (isReg){
                            registerTimer.removeTimer(sender.getName());
                            connectionService.connect((Player) sender,
                                    plugin.getConfig().getString("General.nextConnect", "Hub"),
                                    TypeConnect.MIN);
                        } else {
                            logger.log("Error registration");
                        }
                    });
                }
            });
        }
        return true;
    }
}