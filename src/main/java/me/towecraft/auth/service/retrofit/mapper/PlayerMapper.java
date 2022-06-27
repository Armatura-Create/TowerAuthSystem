package me.towecraft.auth.service.retrofit.mapper;

import lombok.RequiredArgsConstructor;
import me.towecraft.auth.database.entity.PlayerEntity;
import me.towecraft.auth.service.retrofit.dto.PlayerDTO;
import unsave.plugin.context.annotations.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlayerMapper {

    private final AuthMapper authMapper;

    public PlayerDTO toDTO (PlayerEntity entity) {
        return new PlayerDTO()
        .setEmail(entity.getEmail())
        .setName(entity.getUsername())
        .setUuid(entity.getUuid().toString())
        .setPassword(entity.getPassword())
        .setAuthDTO(authMapper.toDTO(entity.getPlayerAuth()));
    }

    public PlayerEntity toEntity(PlayerDTO dto) {
        return new PlayerEntity()
                .setUuid(UUID.fromString(dto.getUuid()))
                .setPlayerAuth(authMapper.toEntity(dto.getAuthDTO()))
                .setPassword(dto.getPassword())
                .setUsername(dto.getName())
                .setEmail(dto.getEmail());
    }

}
