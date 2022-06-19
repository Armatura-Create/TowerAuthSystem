package me.towecraft.auth.service;

import me.towecraft.auth.TAS;
import me.towecraft.auth.listeners.captcha.CaptchaModel;
import me.towecraft.auth.service.connect.ConnectionService;
import me.towecraft.auth.service.connect.TypeConnect;
import me.towecraft.auth.timers.CaptchaTimer;
import me.towecraft.auth.timers.LoginTimer;
import me.towecraft.auth.timers.RegisterTimer;
import me.towecraft.auth.utils.FileMessages;
import me.towecraft.auth.database.repository.PlayerAuthRepository;
import me.towecraft.auth.database.repository.PlayerRepository;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.PostConstruct;
import unsave.plugin.context.annotations.Service;

import java.sql.Timestamp;
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

    @PostConstruct
    private void init() {
        serverConnect = plugin.getConfig().getString("General.nextConnect", "Hub");
        timeSession = plugin.getConfig().getInt("TimeSession", 1800);
    }

    public void verify(Player player, boolean isVerifyCaptcha) {

        if (isVerifyCaptcha)
            captchaService.getMapActions().put(player.getName(), new CaptchaModel());

        playerRepository.findByUsername(player.getName(), result -> {
            if (isVerifyCaptcha && captchaService.getMapActions().get(player.getName()).getCountDoneClick() < 3) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        captchaTimer.regTimer(player);
                        captchaService.showCaptcha(player);
                    }
                }.runTaskLater(plugin, 10L);
            } else if (player.isOnline()) {
                if (result.isPresent()) {
                    if ((timeSession > 0 && result.get().getPlayerAuth().getLastLogin().getTime() >=
                            new Timestamp(System.currentTimeMillis()).getTime() - (timeSession * 1000L) &&
                            result.get().getPlayerAuth().getIpLogin().equals(player.getAddress().getHostName())
                            || result.get().getPlayerAuth().getIpLogin().equals(player.getAddress().getHostName()))) {

                        printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.sessionSuccess"));
                        playerAuthRepository.saveLogin(result.get().getPlayerAuth().setLastLogin(new Date()), null);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                connectionService.connect(player, serverConnect, TypeConnect.MIN);
                            }
                        }.runTaskLater(plugin, 20L);
                    } else {
                        printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.login"));
                        loginTimer.regTimer(player);
                    }
                } else {
                    printMessage.sendMessage(player, fileMessages.getMSG().getStringList("AutoMessages.register"));
                    registerTimer.regTimer(player);
                }
                captchaTimer.removeTimer(player.getName());
            }
        });
    }
}
