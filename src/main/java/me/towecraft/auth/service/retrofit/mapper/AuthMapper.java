package me.towecraft.auth.service.retrofit.mapper;

import me.towecraft.auth.database.entity.PlayerAuthEntity;
import me.towecraft.auth.service.retrofit.dto.AuthDTO;
import unsave.plugin.context.annotations.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class AuthMapper {

    AuthDTO toDTO(PlayerAuthEntity entity) {
        return new AuthDTO()
                .setUuid(entity.getPlayerUuid().toString())
                .setLastLogin(entity.getLastLogin().getTime())
                .setLoginIP(entity.getIpLogin())
                .setRegIP(entity.getIpRegistration())
                .setRecoveryCode(entity.getRecoveryCode())
                .setTimeReg(entity.getTimeRegistration().getTime());
    }

    PlayerAuthEntity toEntity(AuthDTO dto) {
        return new PlayerAuthEntity()
                .setLastLogin(new Date(dto.getLastLogin()))
                .setIpLogin(dto.getLoginIP())
                .setIpRegistration(dto.getRegIP())
                .setPlayerUuid(UUID.fromString(dto.getUuid()))
                .setTimeRegistration(new Date(dto.getTimeReg()))
                .setRecoveryCode(dto.getRecoveryCode());
    }

}
