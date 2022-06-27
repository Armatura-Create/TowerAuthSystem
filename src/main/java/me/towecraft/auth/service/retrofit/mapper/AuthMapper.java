package me.towecraft.auth.service.retrofit.mapper;

import me.towecraft.auth.database.entity.PlayerAuthEntity;
import me.towecraft.auth.service.retrofit.dto.AuthDTO;
import unsave.plugin.context.annotations.Component;

import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Component
public class AuthMapper {

    AuthDTO toDTO(PlayerAuthEntity entity) {
        return new AuthDTO()
                .setUuid(entity.getPlayerUuid().toString())
                .setLastLogin(BigInteger.valueOf(entity.getLastLogin().getTime()))
                .setLoginIP(entity.getIpLogin())
                .setRegIP(entity.getIpRegistration())
                .setRecoveryCode(entity.getRecoveryCode())
                .setTimeReg(BigInteger.valueOf(entity.getTimeRegistration().getTime()));
    }

    PlayerAuthEntity toEntity(AuthDTO dto) {
        return new PlayerAuthEntity()
                .setLastLogin(new Date(dto.getLastLogin().longValue()))
                .setIpLogin(dto.getLoginIP())
                .setIpRegistration(dto.getRegIP())
                .setPlayerUuid(UUID.fromString(dto.getUuid()))
                .setTimeRegistration(new Date(dto.getTimeReg().longValue()))
                .setRecoveryCode(dto.getRecoveryCode());
    }

}
