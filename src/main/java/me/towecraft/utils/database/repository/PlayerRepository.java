package me.towecraft.utils.database.repository;

import me.towecraft.utils.database.entity.PlayerEntity;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {

    void findByUuid(UUID uuid, MysqlCallback<Optional<PlayerEntity>> callback);

    void findByUsername(String username, MysqlCallback<Optional<PlayerEntity>> callback);

    void save(PlayerEntity player, MysqlCallback<Boolean> callback);
}
