package me.towecraft.listeners.captcha;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypeCaptcha {
    SHOW_ONE_ITEM(1),
    SHOW_ALL_ITEM(2),
    NONE(0);

    @Getter
    private final int type;
}
