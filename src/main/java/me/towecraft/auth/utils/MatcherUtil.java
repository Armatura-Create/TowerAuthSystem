package me.towecraft.auth.utils;

import java.util.regex.Pattern;

public final class MatcherUtil {
    public static boolean checkEmail(String email) {
        return Pattern.compile("(?:[A-Za-z\\d!#$%&'*+/=?^_`{|}~-]+(?:\\.[A-Za-z\\d!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[A-Za-z0-9-]*[A-Za-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])").matcher(email).matches();
    }
    public static boolean checkRusSymbol(String string) {
        return Pattern.matches(".*\\p{InCyrillic}.*", string);
    }

    public static boolean checkContainsRusSymbol(String string) {
        for (int i = 0; i < string.length(); i++) {
            if (Character.UnicodeBlock.of(string.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC)) {
                return true;
            }
        }
        return false;
    }
}
