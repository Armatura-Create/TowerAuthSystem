package me.towecraft.listeners.captcha;

import lombok.Data;

@Data
public class CaptchaModel {
    private int countMissClick;
    private int countDoneClick;
    private int fastClick;
    private boolean isClick;
}
