package me.towecraft.utils.mysql;

import me.towecraft.TAS;
import me.towecraft.utils.callbacks.CallbackSQL;
import me.towecraft.utils.models.cache.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;


public class MySQL {

    public static void getPlayer(final Player player, final CallbackSQL<Boolean> callbackSQL) {

        new BukkitRunnable() {
            @Override
            public void run() {
                PoolManager.execute(connection -> {
                    ResultSet set = connection.createStatement().executeQuery("SELECT * FROM playerdata WHERE uuid = '" + player.getUniqueId().toString() + "';");
                    if (set.next()) {
                        if (!set.getString("name").equals(player.getName())) {
                            PoolManager.execute(p3 -> {
                                connection.createStatement().executeUpdate("UPDATE playerdata SET name = '" + player.getName() + "' WHERE uuid = '" + player.getUniqueId().toString() + "'");
                                return null;
                            });
                        }
                        if (TAS.plugin.getPlayerDataList().searchPlayer(player.getName()) == null) {
                            TAS.plugin.getPlayerDataList().addPlayer(new PlayerData(set.getString("uuid"), set.getString("name"), set.getString("email_user"), set.getString("reg_ip"), set.getString("log_ip"), set.getString("password"), set.getTimestamp("first_login"), set.getTimestamp("last_login"), set.getBoolean("valid")));
                        } else {
                            TAS.plugin.getPlayerDataList().modifyPlayer(new PlayerData(set.getString("uuid"), set.getString("name"), set.getString("email_user"), set.getString("reg_ip"), set.getString("log_ip"), set.getString("password"), set.getTimestamp("first_login"), set.getTimestamp("last_login"), set.getBoolean("valid")));
                        }
                        if (callbackSQL != null)
                            callbackSQL.done(true);
                    } else if (TAS.plugin.getPlayerDataList().searchPlayer(player.getName()) == null) {
                        TAS.plugin.getPlayerDataList().addPlayer(new PlayerData(player.getUniqueId().toString(), player.getName(), "null", "null", "null", "null", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), false));
                        if (callbackSQL != null)
                            callbackSQL.done(true);
                    } else {
                        TAS.plugin.getPlayerDataList().modifyPlayer(new PlayerData(player.getUniqueId().toString(), player.getName(), "null", "null", "null", "null", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()), false));
                        if (callbackSQL != null)
                            callbackSQL.done(true);
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(TAS.plugin);
    }

    public static void getPlayerData(Player player, String field, CallbackSQL<String> callbackSQL) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player != null) {
                    PoolManager.execute(connection -> {
                        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM playerdata WHERE uuid = '" + player.getUniqueId().toString() + "';");
                        if (resultSet.next()) {
                            callbackSQL.done(resultSet.getString(field));
                        } else {
                            callbackSQL.done(null);
                        }
                        return null;
                    });
                } else {
                    callbackSQL.error(new SQLException("No found player"));
                }
            }
        }.runTaskAsynchronously(TAS.plugin);
    }

    public static void isPlayerDB(Player player, CallbackSQL<Boolean> callbackSQL) {
        if (player != null) {
            PoolManager.execute(connection -> {
                callbackSQL.done(connection.createStatement().executeQuery("SELECT * FROM playerdata WHERE uuid like '" + player.getUniqueId().toString() + "';").next());
                return null;
            });
        } else {
            callbackSQL.done(false);
        }
    }

    public static void setPlayerData(Player player, String filed, final String data, CallbackSQL<Boolean> callbackSQL) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PoolManager.execute(connection -> {
                    connection.createStatement().executeUpdate("UPDATE playerdata SET " + filed + " = '" + data + "' WHERE uuid like '" + player.getUniqueId().toString() + "'");
                    if (callbackSQL != null)
                        callbackSQL.done(true);
                    return true;
                });
            }
        }.runTaskAsynchronously(TAS.plugin);
    }

    public static void PlayerSQL(Player player, int valid, String email) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PoolManager.execute(connection -> {
                    final Statement statement = connection.createStatement();
                    if (!statement.executeQuery("SELECT * FROM playerdata WHERE uuid like '" + player.getUniqueId().toString() + "';").next()) {
                        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        if (email == null)
                            statement.executeUpdate("INSERT INTO playerdata (`uuid`, `name`, `reg_ip`, `log_ip`, `password`, `first_login`, `last_login`, `valid`, `server`, `lwlogged`) VALUES('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + player.getAddress().getAddress().getHostAddress() + "', '" + player.getAddress().getAddress().getHostAddress() + "', 'null', '" + timestamp.toString() + "', '" + timestamp.toString() + "', '" + valid + "', 'null')");
                        else
                            statement.executeUpdate("INSERT INTO playerdata (`uuid`, `name`, `email_user`, `reg_ip`, `log_ip`, `password`, `first_login`, `last_login`, `valid`, `server`, `lwlogged`) VALUES('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '" + email + "', '" + player.getAddress().getAddress().getHostAddress() + "', '" + player.getAddress().getAddress().getHostAddress() + "', 'null', '" + timestamp.toString() + "', '" + timestamp.toString() + "', '" + valid + "', 'null')");
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(TAS.plugin);
    }

    public static void getPlayers(final CallbackSQL<Boolean> callbackSQL) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PoolManager.execute(connection -> {
                    ResultSet set = connection.createStatement().executeQuery("SELECT * FROM playerdata;");
                    while (set.next()) {
                        TAS.plugin.getPlayerDataList().addPlayer(new PlayerData(set.getString("uuid"), set.getString("name"), set.getString("email_user"), set.getString("reg_ip"), set.getString("log_ip"), set.getString("password"), set.getTimestamp("first_login"), set.getTimestamp("last_login"), set.getBoolean("valid")));
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(TAS.plugin);
    }

    public static void updatePlayer(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                PoolManager.execute(connection -> {
                    ResultSet set = connection.createStatement().executeQuery("SELECT * FROM playerdata WHERE uuid like '" + player.getUniqueId().toString() + "';");
                    if (set.next()) {
                        TAS.plugin.getPlayerDataList().modifyPlayer(new PlayerData(set.getString("uuid"), set.getString("name"), set.getString("email_user"), set.getString("reg_ip"), set.getString("log_ip"), set.getString("password"), set.getTimestamp("first_login"), set.getTimestamp("last_login"), set.getBoolean("valid")));
                    }
                    return null;
                });
            }
        }.runTaskAsynchronously(TAS.plugin);
    }
}
