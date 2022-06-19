package me.towecraft.service;

import me.towecraft.TAS;
import me.towecraft.utils.PluginLogger;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;
import unsave.plugin.context.annotations.PostConstruct;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

@Component
public class RecoveryService {

    @Autowire
    private TAS plugin;
    @Autowire
    private PluginLogger logger;

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

    public void send(String to, String sub, String code, String playerName) {

        File form = new File(this.plugin.getDataFolder(), "form.html");

        if (!form.exists()) {
            logger.log("Not found form.html");
            return;
        }

        try {
            String html = String.join("", Files.readAllLines(form.toPath()));

            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(sub);
            message.setText(html.replace("%code%", code).replace("%player%", playerName));

            Transport.send(message);
            logger.log("Message sent successfully to - " + sub);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
