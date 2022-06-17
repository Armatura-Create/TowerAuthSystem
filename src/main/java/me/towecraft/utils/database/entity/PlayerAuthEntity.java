package me.towecraft.utils.database.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class PlayerAuthEntity {

    private UUID playerUuid;
    private String ipRegistration;
    private String ipLogin;
    private Date lastLogin;
    private Date timeRegistration;
    private Boolean validCaptcha;
}
