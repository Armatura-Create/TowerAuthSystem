package me.towecraft.utils.database;

import lombok.RequiredArgsConstructor;
import me.towecraft.utils.PluginLogger;
import me.towecraft.utils.database.rowMappers.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JDBCTemplate {

    private final DataSource dataSource;
    private final PluginLogger pluginLogger;

    public <T> List<T> query(String query, Object[] params, RowMapper<T> rowMapper) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ResultSet rs = ps.executeQuery();
            List<T> result = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                result.add(rowMapper.mapRow(rs, rs.getRow()));
            }

            return result;
        } catch (SQLException ex) {
            pluginLogger.log(ex.getMessage());
            return new ArrayList<>();
        }
    }

    public <T> Optional<T> queryForObject(String query, Object[] params, RowMapper<T> rowMapper) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rowMapper.mapRow(rs, rs.getRow()));
            }
            return Optional.empty();
        } catch (SQLException ex) {
            pluginLogger.log(ex.getMessage());
            return Optional.empty();
        }
    }

    public int update(String query, Object[] params) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException ex) {
            pluginLogger.log(ex.getMessage());
            return 0;
        }
    }
}
