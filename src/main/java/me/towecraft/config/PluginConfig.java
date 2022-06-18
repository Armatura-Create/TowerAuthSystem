package me.towecraft.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.towecraft.TAS;
import me.towecraft.utils.HashUtil;
import me.towecraft.utils.NameServerService;
import me.towecraft.utils.PrintMessageUtil;
import me.towecraft.utils.PluginLogger;
import me.towecraft.utils.database.JDBCTemplate;
import me.towecraft.utils.database.repository.InitBaseRepository;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Bean;
import unsave.plugin.context.annotations.Configuration;
import unsave.plugin.context.annotations.PostConstruct;

import javax.sql.DataSource;

@Configuration
public class PluginConfig {

    @Autowire
    private TAS plugin;

    @Autowire
    private InitBaseRepository initBaseRepository;

    @Bean
    public PluginLogger getPluginLogger() {
        return new PluginLogger();
    }

    @Bean
    public NameServerService getNameServerService() {
        return new NameServerService();
    }

    @Bean
    public PrintMessageUtil getPrintMessageChatUtil() {
        return new PrintMessageUtil();
    }

    @Bean
    public JDBCTemplate getJDBCTemplate() {
        return new JDBCTemplate(getDataSource(), getPluginLogger());
    }

    @Bean
    public HashUtil getHashUtil() {
        return new HashUtil();
    }

    @Bean
    public DataSource getDataSource() {

        String host = plugin.getConfig().getString("Database.host", "127.0.0.1");
        int port = plugin.getConfig().getInt("Database.port", 3306);
        boolean ssl = plugin.getConfig().getBoolean("Database.ssl", false);
        int minConnections = plugin.getConfig().getInt("Database.minConnections", 5);
        int maxConnections = plugin.getConfig().getInt("Database.maxConnections", 50);
        long timeout = plugin.getConfig().getLong("Database.timeout", 30) * 1000;

        String user = plugin.getConfig().getString("Database.user");
        if (user == null) try {
            throw new Exception("Not found user in config.yml [Database.user]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String password = plugin.getConfig().getString("Database.password");
        if (password == null) try {
            throw new Exception("Not found password in config.yml [Database.password]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String database = plugin.getConfig().getString("Database.database");
        if (database == null) try {
            throw new Exception("Not found database in config.yml [Database.database]");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + ssl + "&characterEncoding=utf8&useConfigs=maxPerformance");
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMinimumIdle(minConnections);
        hikariConfig.setMaximumPoolSize(maxConnections);
        hikariConfig.setConnectionTimeout(timeout);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(hikariConfig);
    }

    @PostConstruct
    public void init() {
        initBaseRepository.initPlayersTable();
        initBaseRepository.initAuthPlayersTable();
    }

}
