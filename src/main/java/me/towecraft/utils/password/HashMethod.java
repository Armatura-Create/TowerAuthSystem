package me.towecraft.utils.password;

import me.towecraft.utils.callbacks.CallbackSQL;
import me.towecraft.utils.mysql.MySQL;
import org.bukkit.entity.Player;

public class HashMethod {
    public static String HashPassword(final Player proxiedPlayer, final String pass) {
        return MD5H(pass);
    }

    public static void MashMatch(final Player proxiedPlayer, final String hashPass, final CallbackSQL<Boolean> callbackSQL) {
        MySQL.getPlayerData(proxiedPlayer, "password", new CallbackSQL<String>() {
            @Override
            public void done(final String sqlPassHash) {
                callbackSQL.done(sqlPassHash.equals(MD5H(hashPass)));
            }

            @Override
            public void error(final Exception ex) {
            }
        });
    }

    private static String MD5H(final String pass) {
        return Hasher.md5(Hasher.md5(pass.toLowerCase()));
    }
}
