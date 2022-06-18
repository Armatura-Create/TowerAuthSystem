package me.towecraft.utils;

public class PluginLogger {
    public void log(String message) {
        java.util.logging.Logger.getLogger("Minecraft").info("[TowerGuiSystem] " + message);
    }
}
