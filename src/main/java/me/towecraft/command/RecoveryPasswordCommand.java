package me.towecraft.command;

import me.towecraft.TAS;
import me.towecraft.service.RecoveryService;
import me.towecraft.utils.PluginLogger;
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

        return false;
    }

    public void getItem(Player player) {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)){
            player.getInventory().setItem(0, null);
        }
    }
}
