package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.user.AuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthorityEntity createAuthoritiy(AuthorityEntity authorityEntity) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO  \"authority\" (user_id, authority) " +
                        "VALUES ( ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1,authorityEntity.getUser().getId());
            ps.setString(2, authorityEntity.getAuthority().name());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authorityEntity.setId(generatedKey);
            return authorityEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    }

