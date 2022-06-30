package me.towecraft.auth.service.retrofit.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(chain = true)
public class AuthDTO {

    private String uuid;
    private String loginIP;
    private String regIP;
    private Long lastLogin;
    private Long timeReg;
    private String recoveryCode;

}
