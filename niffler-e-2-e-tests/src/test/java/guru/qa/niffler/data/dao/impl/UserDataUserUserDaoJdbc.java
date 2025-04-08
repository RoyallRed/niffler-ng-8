package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDataUserUserDaoJdbc implements UserDataUserDao {

    private static final Config CFG = Config.getInstance();
    @Override
    public UserEntity createUser(UserEntity userEntity) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO user (username, currency, firstname, surname, photo,photo_small,full_name) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, userEntity.getUsername());
                ps.setString(2, userEntity.getCurrency().name());
                ps.setString(3, userEntity.getFirstname());
                ps.setString(4, userEntity.getSurname());
                ps.setBytes(5, userEntity.getPhoto());
                ps.setBytes(6, userEntity.getPhotoSmall());
                ps.setString(7, userEntity.getFullname());
                ps.executeUpdate();

                final UUID generatedKey;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedKey = rs.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                userEntity.setId(generatedKey);
                return userEntity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM user WHERE id = ?"
            )) {
                ps.setObject(1, id);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ue = new UserEntity();
                        ue.setId(rs.getObject("id", UUID.class));
                        ue.setUsername(rs.getString("username"));
                        ue.setCurrency(rs.getObject("currency", CurrencyValues.class));
                        ue.setFirstname(rs.getString("firstname"));
                        ue.setSurname(rs.getString("surname"));
                        ue.setPhoto(rs.getBytes("username"));
                        ue.setPhotoSmall(rs.getBytes("username"));
                        ue.setFullname(rs.getString("fullname"));
                        return Optional.of(ue);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM user WHERE username = ?"
            )) {
                ps.setObject(1, username);
                ps.execute();
                try (ResultSet rs = ps.getResultSet()) {
                    if (rs.next()) {
                        UserEntity ue = new UserEntity();
                        ue.setId(rs.getObject("id", UUID.class));
                        ue.setUsername(rs.getString("username"));
                        ue.setCurrency(rs.getObject("currency", CurrencyValues.class));
                        ue.setFirstname(rs.getString("firstname"));
                        ue.setSurname(rs.getString("surname"));
                        ue.setPhoto(rs.getBytes("username"));
                        ue.setPhotoSmall(rs.getBytes("username"));
                        ue.setFullname(rs.getString("fullname"));
                        return Optional.of(ue);
                    } else {
                        return Optional.empty();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity userEntity) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM user WHERE id = ?"
            )) {
                ps.setObject(1, userEntity.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
