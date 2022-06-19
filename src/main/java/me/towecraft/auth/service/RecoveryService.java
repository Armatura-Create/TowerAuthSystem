package me.towecraft.auth.service;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.repository.PlayerAuthRepository;
import me.towecraft.auth.database.repository.PlayerRepository;
import me.towecraft.auth.timers.LoginTimer;
import me.towecraft.auth.timers.RecoveryTimer;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.utils.PluginLogger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Random;

import static org.bukkit.Material.WATCH;

@Component
public class RecoveryService {

    @Autowire
    private TAS plugin;
    @Autowire
    private PluginLogger logger;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private PlayerAuthRepository playerAuthRepository;

    @Autowire
    private PrintMessageService printMessage;

    @Autowire
    private LoginTimer loginTimer;

    @Autowire
    private RecoveryTimer recoveryTimer;

    private Session session;

    @PostConstruct
    private void init() {

        if (plugin.getConfig().getBoolean("SMTP.enable")) {
            logger.log("Start init SMTP");
            Properties props = new Properties();

            String from = plugin.getConfig().getString("SMTP.user");
            if (from == null) try {
                throw new Exception("Not found [SMTP.user] in config.yml");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String password = plugin.getConfig().getString("SMTP.password");
            if (password == null) try {
                throw new Exception("Not found [SMTP.password] in config.yml");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String host = plugin.getConfig().getString("SMTP.host", "smtp.gmail.com");
            int port = plugin.getConfig().getInt("SMTP.port", 465);

            props.put("mail.smtp.host", host);
            props.put("mail.smtp.socketFactory.port", port);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", port);

            session = Session.getDefaultInstance(props,
                    new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, password);
                        }
                    });
        }
    }

    public void send(Player player) {

        playerRepository.findByUsername(player.getName(), result -> result.ifPresent(p -> {
            if (p.getPlayerAuth().getRecoveryCode() == null) {
                String code = getRandomCode();
                p.getPlayerAuth().setRecoveryCode(code);
                playerAuthRepository.saveRecovery(p.getPlayerAuth());
                File form = fileMessages.getFormRecovery();

                if (!form.exists()) {
                    logger.log("Not found form.html");
                    return;
                }

                try {
                    String html = String.join("", Files.readAllLines(form.toPath()));

                    MimeMessage message = new MimeMessage(session);
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(p.getEmail()));
                    message.setSubject("Восстановление пароля");
                    message.setText(html.replace("%code%", code).replace("%player%", player.getName()),
                            "utf-8", "html");

                    Transport.send(message);
                    logger.log("Message sent successfully to - " + p.getEmail());
                    printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.recovery"));

                    player.getInventory().clear();
                    loginTimer.removeTimer(player.getName());
                    recoveryTimer.regTimer(player);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                printMessage.sendMessage(player, fileMessages.getMSG().getString("Commands.recovery.alreadySendCode",
                        "Not found string [Commands.recovery.alreadySendCode] in Message.yml"));
            }
        }));
    }

    public void getItem(Player player) {
        if (plugin.getConfig().getBoolean("SMTP.enable", false)) {

            playerRepository.findByUsername(player.getName(), result -> result.ifPresent(p -> {

                if (p.getEmail() != null && !p.getEmail().trim().isEmpty()) {
                    new BukkitRunnable() {
                        public void run() {
                            if (!player.isOnline()) {
                                return;
                            }

                            ItemStack item = new ItemStack(Material.getMaterial(String.valueOf(WATCH)), 1);
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName("§eВосстановление пароля");
                            item.setItemMeta(meta);

                            player.getInventory().setItem(8, item);
                            player.updateInventory();
                        }
                    }.runTaskLaterAsynchronously(plugin, 10L);
                }
            }));
        }
    }

    private String getRandomCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
