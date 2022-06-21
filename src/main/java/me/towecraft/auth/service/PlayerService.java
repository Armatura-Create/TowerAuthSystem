package me.towecraft.auth.service;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.repository.PlayerAuthRepository;
import me.towecraft.auth.database.repository.PlayerRepository;
import me.towecraft.auth.listeners.captcha.CaptchaModel;
import me.towecraft.auth.listeners.captcha.TypeCaptcha;
import me.towecraft.auth.service.connect.ConnectionService;
import me.towecraft.auth.service.connect.TypeConnect;
import me.towecraft.auth.timers.CaptchaTimer;
import me.towecraft.auth.timers.LoginTimer;
import me.towecraft.auth.timers.RegisterTimer;
import me.towecraft.auth.utils.FileMessages;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.PostConstruct;
import unsave.plugin.context.annotations.Service;

import java.util.Date;

@Service
public class PlayerService {

    @Autowire
    private TAS plugin;

    @Autowire
    private ConnectionService connectionService;

    @Autowire
    private PlayerRepository playerRepository;

    @Autowire
    private PlayerAuthRepository playerAuthRepository;

    @Autowire
    private CaptchaService captchaService;

    @Autowire
    private PrintMessageService printMessage;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private CaptchaTimer captchaTimer;

    @Autowire
    private RegisterTimer registerTimer;

    @Autowire
    private LoginTimer loginTimer;

    private String serverConnect;
    private int timeSession;
    private boolean requiredEmailRegister;

    @PostConstruct
    private void init() {
        serverConnect = plugin.getConfig().getString("General.nextConnect");
        if (serverConnect == null)
            throw new RuntimeException("Not found [General.nextConnect] in config.yml");
        timeSession = plugin.getConfig().getInt("General.timeSessions", 1800);
        requiredEmailRegister = plugin.getConfig().getBoolean("SMTP.enable", false);
    }

    public void verify(Player player, boolean isVerifyCaptcha) {

        if (!isVerifyCaptcha)
            captchaTimer.removeTimer(player);

        playerRepository.findByUsername(player.getName(), result -> {
            if (player.isOnline()) {
                if (result.isPresent()) {
                    if (result.get().getPlayerAuth().getIpLogin().equals(player.getAddress().getHostName())) {
                        captchaService.setTypeCaptcha(player, TypeCaptcha.NONE);
                        if (timeSession > 0 && result.get().getPlayerAuth().getLastLogin().getTime() <=
                                new Date().getTime() - timeSession * 1000L) {
                            loginTimer.regTimer(player);
                            printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.login"));
                        } else {
                            printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.sessionSuccess"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    connectionService.connect(player, serverConnect, TypeConnect.MIN);
                                    playerAuthRepository.saveLogin(result.get().getPlayerAuth().setLastLogin(new Date()), null);
                                    loginTimer.removeTimer(player);
                                }
                            }.runTaskLater(plugin, 20L);
                        }
                    } else if (isVerifyCaptcha) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                captchaTimer.regTimer(player);
                                captchaService.showCaptcha(player);
                            }
                        }.runTaskLater(plugin, 10L);
                    } else {
                        printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.login"));
                        loginTimer.regTimer(player);
                    }
                } else {
                    if (isVerifyCaptcha) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                captchaTimer.regTimer(player);
                                captchaService.showCaptcha(player);
                            }
                        }.runTaskLater(plugin, 10L);
                    } else {
                        printMessage.sendMessage(player, requiredEmailRegister ?
                                fileMessages.getMSG().getStringList("AutoMessages.registerWithEmail")
                                : fileMessages.getMSG().getStringList("AutoMessages.register"));
                        registerTimer.regTimer(player);
                    }
                }
            }
        });
    }
}
