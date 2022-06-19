package me.towecraft.auth.database.rowMappers;

import me.towecraft.auth.database.entity.PlayerAuthEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class PlayerAuthRowMapper implements RowMapper<PlayerAuthEntity> {
    @Override
    public PlayerAuthEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PlayerAuthEntity()
                .setPlayerUuid(UUID.fromString(rs.getString("player_uuid")))
                .setIpLogin(rs.getString("login_ip"))
                .setIpRegistration(rs.getString("reg_ip"))
                .setTimeRegistration(new Date(rs.getLong("time_reg")))
                .setLastLogin(new Date(rs.getLong("last_login")))
                .setRecoveryCode(rs.getString("recovery_code"));
    }
}
