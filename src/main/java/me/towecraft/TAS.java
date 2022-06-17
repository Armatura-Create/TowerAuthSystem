package me.towecraft;

import me.towecraft.utils.FileMessages;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import unsave.plugin.context.context.PluginApplicationContext;

public class TAS extends JavaPlugin {

    private String prefix;
    @Override
    public void onEnable() {

        this.saveDefaultConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        PluginApplicationContext context = new PluginApplicationContext(this);
        prefix = context.getBean(FileMessages.class).getMSG().getString("Prefix", "§6TAS §8» §7");
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
    }

    public String getPrefix() {
        return prefix;
    }
}
