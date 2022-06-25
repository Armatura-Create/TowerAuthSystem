package me.towecraft.auth.listeners.captcha;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CaptchaModel {
    private int countMissClick;
    private int countDoneClick;

    public CaptchaModel incrementMiss() {
        countMissClick++;
        return this;
    }

    public CaptchaModel incrementDone() {
        countDoneClick++;
        return this;
    }
}
