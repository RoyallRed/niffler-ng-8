package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.extractor.CategoryEntityRowExtractor;
import guru.qa.niffler.data.extractor.SpendEntityRowExtractor;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.spendJdbcUrl();


  @Override
  public SpendEntity create(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
              "VALUES ( ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  @Override
  public List<SpendEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return jdbcTemplate.query(
        "SELECT * FROM \"spend\"",
        SpendEntityRowMapper.instance
    );
  }

  @Override
  public SpendEntity update(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
              """
                    UPDATE \"spend\" 
                    SET 
                    username = ?,
                    spend_date = ?,
                    currency = ?,
                    amount = ?,
                    description = ?,
                    category = ?,                        
                    WHERE id = ?
                    """,
              Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category)  {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
              "INSERT INTO \"category\" (name, username, archived) " +
                      "VALUES (?, ?, ?)",
              Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, category.getName());
      ps.setString(2, category.getUsername());
      ps.setBoolean(3, category.isArchived());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    category.setId(generatedKey);
    return category;
  }


  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
            jdbcTemplate.query(
                    "SELECT * FROM \"category\" WHERE id = ?",
                    CategoryEntityRowExtractor.instance,
                    id
            )
    );
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
            jdbcTemplate.query(
                    "SELECT * FROM category WHERE username = ? AND username = ?",
                    CategoryEntityRowExtractor.instance,
                    username,
                    name
            )
    );
  }


  @Override
  public Optional<SpendEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
            jdbcTemplate.query(
                    "SELECT * FROM \"spend\" WHERE id = ?",
                    SpendEntityRowExtractor.instance,
                    id
            )
    );
  }

  @Override
  public Optional<SpendEntity> findByUsernameAndSpendingDescription(String username, String description) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
            jdbcTemplate.query(
                    "SELECT * FROM spend WHERE username = ? AND description = ?",
                    SpendEntityRowExtractor.instance,
                    username,
                    description
            )
    );
  }

  @Override
  public void remove(SpendEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update("DELETE FROM \"spend\" WHERE id = ?", user.getId());
  }


  @Override
  public void removeCategory(CategoryEntity category) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
    jdbcTemplate.update("DELETE FROM \"category\" WHERE id = ?", category.getId());
  }

}
