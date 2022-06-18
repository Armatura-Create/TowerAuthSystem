package me.towecraft.command;

import me.towecraft.TAS;
import me.towecraft.utils.PluginLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

@Component
public class RecoveryPassword implements CommandExecutor {

    @Autowire
    private TAS plugin;

    @Autowire
    private PluginLogger logger;

    @PostConstruct
    private void init() {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)){
            logger.log("Init SMTP");
            plugin.getCommand("rec").setExecutor(this);
            plugin.getCommand("recovery").setExecutor(this);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return false;
    }
}
