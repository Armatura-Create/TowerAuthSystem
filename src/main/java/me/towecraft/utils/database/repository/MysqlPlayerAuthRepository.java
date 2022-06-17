package me.towecraft.utils.database.repository;

import me.towecraft.utils.database.JDBCTemplate;
import me.towecraft.utils.database.entity.PlayerAuthEntity;
import me.towecraft.utils.database.rowMappers.PlayerAuthRowMapper;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Component;

import java.sql.Timestamp;
import java.util.Optional;

@Component
public class MysqlPlayerAuthRepository implements PlayerAuthRepository {

    @Autowire
    private JDBCTemplate jdbcTemplate;

    @Override
    public Optional<PlayerAuthEntity> findByUuid(String uuid) {
        return jdbcTemplate.queryForObject("SELECT * FROM player_auth WHERE player_uuid = ?", new Object[]{uuid}, new PlayerAuthRowMapper<>());
    }

    @Override
    public void saveLogin(PlayerAuthEntity playerAuth) {
        jdbcTemplate.update("UPDATE auth_players SET last_login = ?, ip_login = ?, captcha_valid = true WHERE player_uuid = ?",
                new Object[]{new Timestamp(playerAuth.getLastLogin().getTime()), playerAuth.getIpLogin(), playerAuth.getPlayerUuid().toString()});
    }

    @Override
    public void saveRegister(PlayerAuthEntity playerAuth) {
        jdbcTemplate.update("INSERT INTO auth_players (player_uuid, ip_reg, ip_login, time_reg, last_login, captcha_valid) VALUES (?, ?, ?, ?, ?, true)",
                new Object[]{
                        playerAuth.getPlayerUuid().toString(),
                        playerAuth.getIpRegistration(),
                        playerAuth.getIpLogin(),
                        new Timestamp(playerAuth.getTimeRegistration().getTime()),
                        new Timestamp(playerAuth.getLastLogin().getTime())
        });
    }

    @Override
    public void changePassword(PlayerAuthEntity playerAuth) {
        jdbcTemplate.update("UPDATE auth_players SET ip_reg = ?, ip_login = ?, time_reg = ?, last_login = ?, captcha_valid = true WHERE player_uuid = ?",
                new Object[]{
                        playerAuth.getIpRegistration(),
                        playerAuth.getIpLogin(),
                        new Timestamp(playerAuth.getTimeRegistration().getTime()),
                        new Timestamp(playerAuth.getLastLogin().getTime()),
                        playerAuth.getPlayerUuid().toString()
                });
    }
}
