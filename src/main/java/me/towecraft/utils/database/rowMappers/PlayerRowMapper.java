package me.towecraft.utils.database.rowMappers;

import me.towecraft.utils.database.entity.PlayerEntity;
import me.towecraft.utils.database.repository.PlayerAuthRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerRowMapper<T> implements RowMapper<PlayerEntity> {

    private PlayerAuthRepository playerAuthRepository;
    @Override
    public PlayerEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PlayerEntity()
                .setUuid(UUID.fromString(rs.getString("uuid")))
                .setUsername(rs.getString("name"))
                .setPassword(rs.getString("password"))
                .setEmail(rs.getString("email"))
                .setPlayerAuth(playerAuthRepository.findByUuid(rs.getString("uuid")).orElse(null));
    }
}
