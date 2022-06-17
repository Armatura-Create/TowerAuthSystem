package me.towecraft.utils.database.rowMappers;

import me.towecraft.utils.database.entity.PlayerAuthEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class PlayerAuthRowMapper<T> implements RowMapper<PlayerAuthEntity> {
    @Override
    public PlayerAuthEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PlayerAuthEntity()
                .setPlayerUuid(UUID.fromString(rs.getString("player_uuid")))
                .setIpLogin(rs.getString("ip_login"))
                .setIpRegistration(rs.getString("ip_reg"))
                .setTimeRegistration(rs.getDate("time_reg"))
                .setLastLogin(rs.getDate("last_login"))
                .setValidCaptcha(rs.getBoolean("captcha_valid"));
    }
}
