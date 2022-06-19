package me.towecraft.auth.database.repository;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.JDBCTemplate;
import me.towecraft.auth.database.entity.PlayerAuthEntity;
import me.towecraft.auth.database.rowMappers.PlayerAuthRowMapper;
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
        return jdbcTemplate.queryForObject("SELECT * FROM auth_data WHERE player_uuid = ?;",
                new Object[]{uuid}, new PlayerAuthRowMapper()
        );
    }

    @Override
    public int save(PlayerAuthEntity playerAuth) {
        return jdbcTemplate.update("INSERT INTO auth_data (player_uuid, login_ip, reg_ip, time_reg, last_login) VALUES (?, ?, ?, ?, ?);",
                new Object[]{
                        playerAuth.getPlayerUuid().toString(),
                        playerAuth.getIpLogin(),
                        playerAuth.getIpRegistration(),
                        playerAuth.getTimeRegistration().getTime(),
                        playerAuth.getLastLogin().getTime()
                });
    }

    @Override
    public void saveLogin(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                int result = jdbcTemplate.update("UPDATE auth_data SET last_login = ?, login_ip = ?, recovery_code = NULL WHERE player_uuid = ?;",
                        new Object[]{
                                playerAuth.getLastLogin().getTime(),
                                playerAuth.getIpLogin(),
                                playerAuth.getPlayerUuid().toString()
                        });
                if (callback != null) {
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
                            jdbcTemplate.update("UPDATE auth_data SET reg_ip = ?, login_ip = ?, time_reg = ?, last_login = ? WHERE player_uuid = ?;",
                                    new Object[]{
                                            playerAuth.getIpRegistration(),
                                            playerAuth.getIpLogin(),
                                            playerAuth.getTimeRegistration().getTime(),
                                            playerAuth.getLastLogin().getTime(),
                                            playerAuth.getPlayerUuid().toString()
                                    });
                    callback.callback(result == 1);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void saveRecovery(PlayerAuthEntity playerAuth) {
        jdbcTemplate.update("UPDATE auth_data SET recovery_code = ? WHERE player_uuid = ?;",
                new Object[]{
                        playerAuth.getRecoveryCode(),
                        playerAuth.getPlayerUuid().toString()
                });
    }
}
