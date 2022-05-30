package me.towecraft.utils;

public enum TypeMessage {
    LOGIN("AutoMessages.login"),
    REGISTER("AutoMessages.register"),
    SESSION("AutoMessages.session");

    private String path;

    TypeMessage(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
