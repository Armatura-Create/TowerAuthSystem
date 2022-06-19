package me.towecraft.auth.listeners.captcha;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypeCaptcha {
    SHOW_ONE_ITEM_HIDE_DONE(1),
    SHOW_ONE_ITEM(2),
    SHOW_ALL_ITEM(3),
    RANDOM(4),
    NONE(0);

    @Getter
    private final int type;
}
