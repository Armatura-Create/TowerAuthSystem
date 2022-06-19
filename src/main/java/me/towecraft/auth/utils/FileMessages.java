package me.towecraft.auth.utils;

import com.google.common.io.ByteStreams;
import me.towecraft.auth.TAS;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.PostConstruct;
import unsave.plugin.context.annotations.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FileMessages {

    @Autowire
    private TAS plugin;
    private Configuration config;

    @PostConstruct
    public void init() {
        createMessages();
    }

    public void createMessages() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }

        File file = new File(this.plugin.getDataFolder(), "Messages.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream resourceAsStream = this.plugin.getResource("Messages.yml")) {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        ByteStreams.copy(resourceAsStream, fileOutputStream);
                        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "Messages.yml"));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return;
            } catch (IOException ex2) {
                throw new RuntimeException("Unable to create config file", ex2);
            }
        }
        YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "Messages.yml"));
    }

    public Configuration getMSG() {
        return config;
    }
}
