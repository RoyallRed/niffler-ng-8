package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.authJdbcUrl();

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      user.setId(generatedKey);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    Map<UUID,AuthUserEntity> userCashe = new ConcurrentHashMap<>();
    UUID userId = null;

    String sql="SELECT " +
            "u.id AS user_id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked,u.credentials_non_expired, " +
            "a.authority, a.id AS authority_id" +
            "FROM \"user\" AS u" +
            "INNER JOIN public.authority a ON u.id = a.user_id" +
            "WHERE u.id = ?;";
    try (PreparedStatement ps = holder(url).connection().prepareStatement(sql)) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          userId=rs.getObject("user_id", UUID.class);

          AuthUserEntity user = userCashe.get(userId);
          if (user ==null){
            AuthUserEntity  newUser = new AuthUserEntity();
            user.setId(rs.getObject("user_id", UUID.class));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
            user = newUser;
          }
            AuthorityEntity authorityEntity = new AuthorityEntity();
            authorityEntity.setUser(user);
            authorityEntity.setId(rs.getObject("authority_id", UUID.class));
            authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
            user.addAuthorities(authorityEntity);

         }
        return Optional.ofNullable(userCashe.get(userId));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<AuthUserEntity> findAll() {
    String sql = "SELECT " +
            "u.id AS user_id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired, " +
            "a.authority, a.id AS authority_id " +
            "FROM \"user\" AS u " +
            "INNER JOIN public.authority a ON u.id = a.user_id;";

    try (PreparedStatement ps = holder(url).connection().prepareStatement(sql)) {
      ps.execute();
      List<AuthUserEntity> result = new ArrayList<>();
      Map<UUID, AuthUserEntity> userCache = new HashMap<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          UUID userId = rs.getObject("user_id", UUID.class);
          AuthUserEntity user = userCache.get(userId);
          if (user == null) {
            user = new AuthUserEntity();
            user.setId(userId);
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
            userCache.put(userId, user);
            result.add(user);
          }
          AuthorityEntity authorityEntity = new AuthorityEntity();
          authorityEntity.setId(rs.getObject("authority_id", UUID.class));
          authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
          user.addAuthorities(authorityEntity);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            """
                       UPDATE \"user\" 
                       SET 
                       username = ?,
                       password = ?,
                       enabled = ?,
                       account_non_expired = ?,
                       account_non_locked = ?,
                       credentials_non_expired = ?
                       WHERE id = ?
                       """
    )) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());
      ps.setObject(7, user.getId());
      ps.executeUpdate();
      return user;
    } catch (SQLException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    String sql = """
            SELECT a.id AS authority_id,
                   a.authority,
                   u.id AS user_id,
                   u.username,
                   u.password,
                   u.enabled,
                   u.account_non_expired,
                   u.account_non_locked,
                   u.credentials_non_expired
            FROM "user" u
            JOIN authority a ON u.id = a.user_id
            WHERE u.username = ?;
            """;

    try (PreparedStatement ps = holder(url).connection().prepareStatement(sql)) {
      ps.setObject(1, username);
      ps.execute();

      AuthUserEntity user = null;
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          if (user == null) {
            user = new AuthUserEntity();
            user.setId(rs.getObject("user_id", UUID.class));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          }
          AuthorityEntity authorityEntity = new AuthorityEntity();
          authorityEntity.setId(rs.getObject("authority_id", UUID.class));
          authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
          if (user != null) {
            user.addAuthorities(authorityEntity);
          }
        }
      }

      return Optional.ofNullable(user);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(AuthUserEntity user) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
