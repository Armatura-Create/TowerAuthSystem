package me.towecraft.utils.database.repository;

import me.towecraft.utils.database.entity.PlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {

    List<PlayerEntity> findAll();

    Optional<PlayerEntity> findByUuid(UUID uuid);

    Optional<PlayerEntity> findByUsername(String username);

    void save(PlayerEntity player);
}
