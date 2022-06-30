package me.towecraft.auth.database.repository;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.JDBCTemplate;
import me.towecraft.auth.database.entity.PlayerEntity;
import me.towecraft.auth.database.rowMappers.PlayerRowMapper;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MysqlPlayerRepository implements PlayerRepository {

    @Autowire
    private JDBCTemplate jdbcTemplate;

    @Autowire
    private TAS plugin;

    @Autowire
    private PlayerAuthRepository playerAuthRepository;

    @Override
    public void findByUuid(UUID uuid, MysqlCallback<Optional<PlayerEntity>> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {

                Optional<PlayerEntity> playerAuthEntity = jdbcTemplate.queryForObject("SELECT * FROM players WHERE uuid = ?;", new Object[]{uuid.toString()}, new PlayerRowMapper<>(playerAuthRepository));

                if (callback != null) {
                    callback.callback(playerAuthEntity);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void findByUsername(String username, MysqlCallback<Optional<PlayerEntity>> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {

                Optional<PlayerEntity> playerAuthEntity = jdbcTemplate.queryForObject("SELECT * FROM players WHERE name = ?;", new Object[]{username}, new PlayerRowMapper<>(playerAuthRepository));

                if (callback != null) {
                    callback.callback(playerAuthEntity);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void findByEmail(String email, MysqlCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (email == null && callback != null) callback.callback(false);
                else {
                    Optional<PlayerEntity> playerAuthEntity = jdbcTemplate.queryForObject("SELECT * FROM players WHERE email = ?;", new Object[]{email}, new PlayerRowMapper<>(playerAuthRepository));

                    if (callback != null) {
                        callback.callback(playerAuthEntity.isPresent());
                    }
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void save(PlayerEntity player, MysqlCallback<Boolean> callback) {
        new BukkitRunnable() {
            @Override
            public void run() {

                int result = jdbcTemplate.update("INSERT INTO players (uuid, name, password, email) VALUES (?,?,?,?);",
                        new Object[]{
                                player.getUuid().toString(),
                                player.getUsername(),
                                player.getPassword(),
                                player.getEmail()
                        });

                if (result == 1)
                    result = playerAuthRepository.save(player.getPlayerAuth());

                if (callback != null) {
                    callback.callback(result == 1);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void savePassword(PlayerEntity player) {
        new BukkitRunnable() {
            @Override
            public void run() {

                int result = jdbcTemplate.update("UPDATE players SET password = ? WHERE uuid = ?;",
                        new Object[]{
                                player.getPassword(),
                                player.getUuid().toString()
                        });
            }
        }.runTaskAsynchronously(plugin);
    }
}