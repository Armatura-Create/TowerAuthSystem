package me.towecraft.auth.utils;

import unsave.plugin.context.annotations.Component;

@Component
public class PluginLogger {
    public void log(String message) {
        java.util.logging.Logger.getLogger("Minecraft").info("[TowerGuiSystem] " + message);
    }
}
