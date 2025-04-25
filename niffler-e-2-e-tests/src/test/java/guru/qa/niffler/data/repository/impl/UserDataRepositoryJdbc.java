package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;
import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserDataRepositoryJdbc implements UserDataRepository {

    private static final Config CFG = Config.getInstance();
    private final String url = CFG.userdataJdbcUrl();

    @Override
    public UserEntity createUser(UserEntity user) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ? )",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getFullname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());
            ps.executeUpdate();
            final UUID generatedUserId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getObject("id", UUID.class);
                } else {
                    throw new IllegalStateException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement("SELECT * FROM \"user\" WHERE id = ? ")) {
            ps.setObject(1, id);

            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                UserEntity result = new UserEntity();
                result.setId(rs.getObject("id", UUID.class));
                result.setUsername(rs.getString("username"));
                result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                result.setFirstname(rs.getString("firstname"));
                result.setSurname(rs.getString("surname"));
                result.setPhoto(rs.getBytes("photo"));
                result.setPhotoSmall(rs.getBytes("photo_small"));
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement("SELECT * FROM \"user\" WHERE username = ? ")) {
            ps.setObject(1, username);

            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) {
                UserEntity result = new UserEntity();
                result.setId(rs.getObject("id", UUID.class));
                result.setUsername(rs.getString("username"));
                result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                result.setFirstname(rs.getString("firstname"));
                result.setSurname(rs.getString("surname"));
                result.setPhoto(rs.getBytes("photo"));
                result.setPhotoSmall(rs.getBytes("photo_small"));
                return Optional.of(result);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try (

                PreparedStatement ps = holder(url).connection().prepareStatement(
                        "INSERT INTO \"friendship\" (requester_id, addressee_id,status) VALUES (?, ?,?)")) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, ACCEPTED.name());
            ps.executeUpdate();

            ps.setObject(1, addressee.getId());
            ps.setObject(2, requester.getId());
            ps.setString(3, ACCEPTED.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void addFriendshipRequest(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(url).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ? )"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, PENDING.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
