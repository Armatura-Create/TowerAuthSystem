package me.towecraft.utils.database.repository;

import me.towecraft.utils.database.entity.PlayerAuthEntity;

import java.util.Optional;

public interface PlayerAuthRepository {

    Optional<PlayerAuthEntity> findByUuid(String uuid);

    void saveLogin(PlayerAuthEntity playerAuth);

    void saveRegister(PlayerAuthEntity playerAuth);

    void changePassword(PlayerAuthEntity playerAuth);

}
