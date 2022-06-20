package me.towecraft.auth.listeners.captcha;

import lombok.Data;

@Data
public class CaptchaModel {
    private int countMissClick;
    private int countDoneClick;
}
