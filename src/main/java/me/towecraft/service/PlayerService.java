package me.towecraft.service;

import me.towecraft.TAS;
import me.towecraft.listeners.captcha.CaptchaListener;
import me.towecraft.service.connect.ConnectionService;
import me.towecraft.service.connect.TypeConnect;
import me.towecraft.timers.CaptchaTimer;
import me.towecraft.timers.RegisterTimer;
import me.towecraft.utils.FileMessages;
import me.towecraft.utils.PrintMessageUtil;
import me.towecraft.utils.database.repository.PlayerAuthRepository;
import me.towecraft.utils.database.repository.PlayerRepository;
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
    private CaptchaListener captchaListener;

    @Autowire
    private PrintMessageUtil printMessageUtil;

    @Autowire
    private FileMessages fileMessages;

    @Autowire
    private CaptchaTimer captchaTimer;

    @Autowire
    private RegisterTimer registerTimer;

    private String serverConnect;
    private int timeSession;

    @PostConstruct
    private void init() {
        serverConnect = plugin.getConfig().getString("NextConnect", "Hub");
        timeSession = plugin.getConfig().getInt("TimeSession", 1800);
    }

    public void verify(Player player, boolean isVerifyCaptcha) {

        playerRepository.findByUsername(player.getName(), result -> {
            if (isVerifyCaptcha && captchaListener.getCountDoneClick().get(player.getName()) < 3) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        captchaTimer.logTimer(player);
                        captchaListener.showCaptcha(player);
                    }
                }.runTaskLater(plugin, 10L);
            } else if (player.isOnline()) {
                if (result.isPresent()) {
                    if (result.get().getPlayerAuth().getLastLogin().getTime() >=
                            new Timestamp(System.currentTimeMillis()).getTime() - (timeSession * 1000L)) {
                        printMessageUtil.sendMessage(player, fileMessages.getMSG().getStringList("sessionSuccess"));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                connectionService.connect(player, serverConnect, TypeConnect.MIN);
                            }
                        }.runTaskLater(plugin, 20L);
                    } else{
//                        playerAuthRepository.saveLogin(result.get().getPlayerAuth().setLastLogin(new Date()), null);

                        printMessageUtil.sendMessage(player, fileMessages.getMSG().getStringList("login"));
                    }
                } else {
                    printMessageUtil.sendMessage(player, fileMessages.getMSG().getStringList("register"));
                    registerTimer.regTimer(player);
                }
                captchaTimer.removeTimer(player.getName());
            }
        });
    }
}
