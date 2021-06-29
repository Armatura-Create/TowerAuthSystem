package me.towecraft.utils.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.towecraft.utils.Constants;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PoolManager {
    private static HikariDataSource hikari;
    private static String address;
    private static String database;
    private static String port;
    private static String username;
    private static String password;
    private static Boolean ssl;
    private static int size;

    public static void connectDB() {

        PoolManager.address = "217.24.160.92";
        PoolManager.database = "TowerCraft";
        PoolManager.port = "3306";
        PoolManager.username = Constants.DATA_BASE.user;
        PoolManager.password = Constants.DATA_BASE.pass;
        PoolManager.ssl = false;
        PoolManager.size = 10;

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + PoolManager.address + ":" + PoolManager.port + "/" + PoolManager.database + "?useSSL=" + PoolManager.ssl + "&characterEncoding=utf8&useConfigs=maxPerformance");
        hikariConfig.setUsername(PoolManager.username);
        hikariConfig.setPassword(PoolManager.password);
        hikariConfig.setMaximumPoolSize(PoolManager.size);
        hikariConfig.setConnectionTimeout(25000L);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        PoolManager.hikari = new HikariDataSource(hikariConfig);
    }

    public static <T> T execute(final ConnectionCallback<T> connectionCallback) {
        try (final Connection connection = PoolManager.hikari.getConnection()) {
            return connectionCallback.doInConnection(connection);
        } catch (SQLException ex) {
            throw new IllegalStateException("Error during execution.", ex);
        } finally {
            closeConnection();
        }
    }

    public static void closeConnection() {
        try {
            PoolManager.hikari.getConnection().close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void createPlayerTable() {
        execute(connection -> {
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS playerdata(uuid VARCHAR(36) PRIMARY KEY  NOT NULL,name VARCHAR(30) NOT NULL,email VARCHAR(80),reg_ip VARCHAR(20) NOT NULL,log_ip VARCHAR(20) NOT NULL,password VARCHAR(256) NOT NULL, first_login timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, last_login timestamp NOT NULL DEFAULT '" + new Timestamp(System.currentTimeMillis()) + "',valid INT(1) NOT NULL;");
            return null;
        });
    }

    public static void resetNames() {
        execute(connection -> {
            connection.createStatement().executeUpdate("DELETE FROM playernames;");
            return null;
        });
    }

    public interface ConnectionCallback<T> {
        T doInConnection(final Connection connection) throws SQLException;
    }
}
