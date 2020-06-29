package me.towecraft.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageHandler {
    public static BaseComponent sendMSG(final String msg) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', msg));
    }
}
