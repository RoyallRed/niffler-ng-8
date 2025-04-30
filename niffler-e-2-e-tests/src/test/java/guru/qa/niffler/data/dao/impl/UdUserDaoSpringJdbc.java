package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.extractor.UdUserEntityExtractor;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;

public class UdUserDaoSpringJdbc implements UdUserDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.userdataJdbcUrl();

  @Override
  public UserEntity create(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
              "VALUES (?,?,?,?,?,?,?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setBytes(5, user.getPhoto());
      ps.setBytes(6, user.getPhotoSmall());
      ps.setString(7, user.getFullname());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);
    return user;
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    String sql = """
            SELECT u.id AS user_id,u.username,
                   u.currency,
                   u.firstname,
                   u.surname,
                   u.photo,
                   u.photo_small,
                   f.requester_id,f.addressee_id,f.status,f.created_date
            FROM "user" AS u
                      JOIN friendship f ON u.id = f.requester_id
            WHERE u.id =?
            """;
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
        jdbcTemplate.query(
                sql,
            UdUserEntityExtractor.instance,
            id
        )
    );
  }

  @Override
  public List<UserEntity> findAll() {
    String sql = """
            SELECT u.id AS user_id,u.username,
                   u.currency,
                   u.firstname,
                   u.surname,
                   u.photo,
                   u.photo_small,
                   f.requester_id,f.addressee_id,f.status,f.created_date
            FROM "user" AS u
                      JOIN friendship f ON u.id = f.requester_id
            """;
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return jdbcTemplate.query(
            sql,
        UdUserEntityRowMapper.instance
    );
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    String sql = """
            SELECT u.id AS user_id,u.username,
                   u.currency,
                   u.firstname,
                   u.surname,
                   u.photo,
                   u.photo_small,
                   f.requester_id,f.addressee_id,f.status,f.created_date
            FROM "user" AS u
                      JOIN friendship f ON u.id = f.requester_id
            WHERE u.username =?
            """;
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
            jdbcTemplate.query(
                    sql,
                    UdUserEntityExtractor.instance,
                    username
            ));
  }


  @Override
  public void sendInvitation(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
      KeyHolder kh = new GeneratedKeyHolder();
      jdbcTemplate.update(con -> {
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO \"friendship\" (requester_id, addressee_id) VALUES (?, ?)");

            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setObject(3, PENDING);


        return ps;
      }, kh);
    }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.batchUpdate(
            "INSERT INTO \"friendship\" (requester_id, addressee_id) VALUES (?, ?)",
            new BatchPreparedStatementSetter() {
              @Override
              public void setValues(PreparedStatement ps, int i) throws SQLException {
                if (i == 0) {
                  ps.setObject(1, requester.getId());
                  ps.setObject(2, addressee.getId());
                  ps.setObject(3, ACCEPTED);
                } else if (i == 1) {
                  ps.setObject(1, addressee.getId());
                  ps.setObject(2, requester.getId());
                  ps.setObject(3, ACCEPTED);
                }
              }

              @Override
              public int getBatchSize() {
                return 2;
              }
            }
    );

  }

  @Override
  public void remove(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
  }

}





