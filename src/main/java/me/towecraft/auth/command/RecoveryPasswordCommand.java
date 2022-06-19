package me.towecraft.auth.command;

import me.towecraft.auth.TAS;
import me.towecraft.auth.service.PrintMessageService;
import me.towecraft.auth.service.RecoveryService;
import me.towecraft.auth.utils.PluginLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

@Component
public class RecoveryPasswordCommand implements CommandExecutor {

    @Autowire
    private TAS plugin;

    @Autowire
    private PluginLogger logger;

    @Autowire
    private RecoveryService recoveryService;

    @Autowire
    private PrintMessageService printMessage;

    @PostConstruct
    private void init() {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)){
            logger.log("Init command /password");
            plugin.getCommand("pass").setExecutor(this);
            plugin.getCommand("password").setExecutor(this);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        printMessage.sendMessage((Player) sender, "Coming soon");
        return false;
    }
}
