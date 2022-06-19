package me.towecraft.utils.database.repository;

import me.towecraft.utils.database.entity.PlayerAuthEntity;

import java.util.Optional;

public interface PlayerAuthRepository {

    Optional<PlayerAuthEntity> findByUuid(String uuid);

    void saveLogin(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback);

    void saveRegister(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback);

    void changePassword(PlayerAuthEntity playerAuth, MysqlCallback<Boolean> callback);

    void saveRecovery(PlayerAuthEntity playerAuth);
}
