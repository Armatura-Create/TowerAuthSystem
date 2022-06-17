package me.towecraft.utils.database.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain = true)
public class PlayerEntity {

    private UUID uuid;
    private String username;
    private String password;
    private String email;
    private PlayerAuthEntity playerAuth;

}
