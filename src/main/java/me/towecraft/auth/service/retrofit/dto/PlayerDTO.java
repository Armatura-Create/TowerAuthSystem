package me.towecraft.auth.service.retrofit.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PlayerDTO {

    private String uuid;
    private String name;
    private String email;
    private String password;
    private AuthDTO authDTO;

}
