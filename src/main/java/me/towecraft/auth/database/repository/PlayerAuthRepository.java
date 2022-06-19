package me.towecraft.auth.database.repository;

import me.towecraft.auth.database.entity.PlayerAuthEntity;

import java.util.Optional;

public interface PlayerAuthRepository {

    Optional<PlayerAuthEntity> findByUuid(String uuid);

    int save(PlayerAuthEntity playerAuth);

    void saveLogin(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback);

    void changePassword(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback);

    void saveRecovery(PlayerAuthEntity playerAuth);
}
