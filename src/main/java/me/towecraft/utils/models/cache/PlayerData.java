package me.towecraft.utils.models.cache;

import java.sql.Timestamp;

public class PlayerData {
    private String uuid;
    private String name;
    private String email;
    private String reg_ip;
    private String log_ip;
    private String password;
    private Timestamp first_login;
    private Timestamp last_login;
    private boolean valid;

    public PlayerData() {
    }

    public PlayerData(final String uuid, final String name, final String email, final String reg_ip, final String log_ip, final String password, final Timestamp first_login, final Timestamp last_login, final boolean valid) {
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.reg_ip = reg_ip;
        this.log_ip = log_ip;
        this.password = password;
        this.first_login = first_login;
        this.last_login = last_login;
        this.valid = valid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReg_ip() {
        return reg_ip;
    }

    public void setReg_ip(String reg_ip) {
        this.reg_ip = reg_ip;
    }

    public String getLog_ip() {
        return log_ip;
    }

    public void setLog_ip(String log_ip) {
        this.log_ip = log_ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getFirst_login() {
        return first_login;
    }

    public void setFirst_login(Timestamp first_login) {
        this.first_login = first_login;
    }

    public Timestamp getLast_login() {
        return last_login;
    }

    public void setLast_login(Timestamp last_login) {
        this.last_login = last_login;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}