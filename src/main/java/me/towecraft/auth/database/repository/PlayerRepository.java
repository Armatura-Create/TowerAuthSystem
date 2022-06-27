package me.towecraft.auth.database.repository;

import me.towecraft.auth.database.entity.PlayerEntity;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {

    void findByUuid(UUID uuid, RepositoryCallback<Optional<PlayerEntity>> callback);

    void findByUsername(String username, RepositoryCallback<Optional<PlayerEntity>> callback);

    void save(PlayerEntity player, RepositoryCallback<Boolean> callback);

    void savePassword(PlayerEntity player);

    void findByEmail(String email, RepositoryCallback<Boolean> callback);
}
