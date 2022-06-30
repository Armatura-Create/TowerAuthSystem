package me.towecraft.auth.database.repository;

import me.towecraft.auth.database.JDBCTemplate;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.PostConstruct;
import unsave.plugin.context.annotations.Repository;

@Repository
public class MysqlInitBaseRepository implements InitBaseRepository {

    @Autowire
    private JDBCTemplate jdbcTemplate;

    @Override
    public void initPlayersTable() {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS players (" +
                "uuid CHAR(36) NOT NULL PRIMARY KEY," +
                "name VARCHAR(70) NOT NULL UNIQUE," +
                "email VARCHAR(70) NULL DEFAULT NULL UNIQUE," +
                "password VARCHAR(70) NOT NULL);", new Object[0]);
    }

    @Override
    public void initAuthPlayersTable() {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS auth_data (" +
                "uuid CHAR(36) NOT NULL PRIMARY KEY REFERENCES players(uuid)," +
                "login_ip VARCHAR(15) NOT NULL," +
                "reg_ip VARCHAR(15) NOT NULL," +
                "last_login BIGINT(20) NULL DEFAULT NULL," +
                "time_reg BIGINT(20) NULL DEFAULT NULL," +
                "recovery_code CHAR(6) DEFAULT NULL);", new Object[0]);
    }

    @PostConstruct
    private void init() {
        initPlayersTable();
        initAuthPlayersTable();
    }
}