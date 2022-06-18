package me.towecraft.utils.database.repository;

import me.towecraft.TAS;
import me.towecraft.utils.database.JDBCTemplate;
import me.towecraft.utils.database.entity.PlayerAuthEntity;
import me.towecraft.utils.database.rowMappers.PlayerAuthRowMapper;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class MysqlPlayerAuthRepository implements PlayerAuthRepository {

    @Autowire
    private JDBCTemplate jdbcTemplate;

    @Autowire
    private TAS plugin;

    @Override
    public Optional<PlayerAuthEntity> findByUuid(String uuid) {
        return jdbcTemplate.queryForObject("SELECT * FROM player_auth WHERE player_uuid = ?;", new Object[]{uuid}, new PlayerAuthRowMapper<>());
    }

    @Override
    public void saveLogin(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (callback != null) {
                    int result = jdbcTemplate.update("UPDATE auth_players SET last_login = ?, ip_login = ?, captcha_valid = true WHERE player_uuid = ?;",
                            new Object[]{new Timestamp(playerAuth.getLastLogin().getTime()), playerAuth.getIpLogin(), playerAuth.getPlayerUuid().toString()});
                    callback.callback(result == 1);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void saveRegister(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (callback != null) {
                    int result = jdbcTemplate.update("INSERT INTO auth_players (player_uuid, ip_reg, ip_login, time_reg, last_login, captcha_valid) VALUES (?, ?, ?, ?, ?, true);",
                            new Object[]{
                                    playerAuth.getPlayerUuid().toString(),
                                    playerAuth.getIpRegistration(),
                                    playerAuth.getIpLogin(),
                                    new Timestamp(playerAuth.getTimeRegistration().getTime()),
                                    new Timestamp(playerAuth.getLastLogin().getTime())
                            });
                    callback.callback(result == 1);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void changePassword(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (callback != null) {
                    int result =
                            jdbcTemplate.update("UPDATE auth_players SET ip_reg = ?, ip_login = ?, time_reg = ?, last_login = ?, captcha_valid = true WHERE player_uuid = ?;",
                                    new Object[]{
                                            playerAuth.getIpRegistration(),
                                            playerAuth.getIpLogin(),
                                            new Timestamp(playerAuth.getTimeRegistration().getTime()),
                                            new Timestamp(playerAuth.getLastLogin().getTime()),
                                            playerAuth.getPlayerUuid().toString()
                                    });
                    callback.callback(result == 1);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
