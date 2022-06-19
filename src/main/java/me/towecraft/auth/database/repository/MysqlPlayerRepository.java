package me.towecraft.auth.database.repository;

import me.towecraft.auth.TAS;
import me.towecraft.auth.database.JDBCTemplate;
import me.towecraft.auth.database.entity.PlayerEntity;
import me.towecraft.auth.database.rowMappers.PlayerRowMapper;
import org.bukkit.scheduler.BukkitRunnable;
import unsave.plugin.context.annotations.Autowire;
import unsave.plugin.context.annotations.Repository;

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

                Optional<PlayerEntity> playerAuthEntity = jdbcTemplate.queryForObject("SELECT * FROM auth_players WHERE player_uuid = ?", new Object[]{uuid.toString()}, new PlayerRowMapper<>(playerAuthRepository));

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

                Optional<PlayerEntity> playerAuthEntity = jdbcTemplate.queryForObject("SELECT * FROM auth_players WHERE name = ?", new Object[]{username}, new PlayerRowMapper<>(playerAuthRepository));

                if (callback != null) {
                    callback.callback(playerAuthEntity);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    @Override
    public void save(PlayerEntity player, MysqlCallback<Boolean> callback) {

    }
}
