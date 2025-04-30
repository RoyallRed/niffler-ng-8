package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.spendJdbcUrl();

  @Override
  public SpendEntity create(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
            "VALUES ( ?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      spend.setId(generatedKey);
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<SpendEntity> findAll() {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM spend")) {
      ps.execute();
      List<SpendEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject("id", UUID.class));
          se.setUsername(rs.getString("username"));
          se.setSpendDate(rs.getDate("spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          se.setAmount(rs.getDouble("amount"));
          se.setDescription(rs.getString("description"));
          CategoryEntity category = new CategoryEntity();
          category.setId(rs.getObject("category_id", UUID.class));
          se.setCategory(category);
          result.add(se);
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public SpendEntity update(SpendEntity spend) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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
                    """
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, (Date) spend.getSpendDate());
      ps.setObject(3, CurrencyValues.valueOf(spend.getCurrency().name()));
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      ps.executeUpdate();
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "INSERT INTO \"category\" (name, username, archived) " +
                    "VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getUsername());
      ps.setBoolean(3, category.isArchived());
      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      category.setId(generatedKey);
      return category;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    Map<UUID, CategoryEntity> userCashe = new ConcurrentHashMap<>();
    UUID categoryId = null;

    String sql="SELECT * FROM category WHERE id =?";
    try (PreparedStatement ps = holder(url).connection().prepareStatement(sql)) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          categoryId=rs.getObject("user_id", UUID.class);

          CategoryEntity category = userCashe.get(categoryId);
          if (category ==null){
            CategoryEntity  newUser = new CategoryEntity();
            category.setId(rs.getObject("user_id", UUID.class));
            category.setUsername(rs.getString("username"));
            category.setName(rs.getString("name"));
            category.setArchived(rs.getBoolean("archived"));
            category = newUser;
          }
        }
        return Optional.ofNullable(userCashe.get(categoryId));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "SELECT * FROM category WHERE username = ? AND username = ?"
    )) {
      ps.setObject(1, username);
      ps.setObject(2, name);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          CategoryEntity  ce = new CategoryEntity();
          ce.setId(rs.getObject("user_id", UUID.class));
          ce.setUsername(rs.getString("username"));
          ce.setName(rs.getString("name"));
          ce.setArchived(rs.getBoolean("archived"));
          return Optional.of(ce);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "SELECT * FROM spend WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject( "id", UUID.class));
          se.setUsername(rs.getString( "username"));
          se.setSpendDate(rs.getDate( "spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString( "currency")));
          se.setAmount(rs.getDouble( "amount"));
          se.setDescription(rs.getString( "description"));
          return Optional.of(se);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<SpendEntity> findByUsernameAndSpendingDescription(String username, String description) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
            "SELECT * FROM spend WHERE username = ? AND description = ?"
    )) {
      ps.setObject(1, username);
      ps.setObject(2, description);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          SpendEntity se = new SpendEntity();
          se.setId(rs.getObject( "id", UUID.class));
          se.setUsername(rs.getString( "username"));
          se.setSpendDate(rs.getDate( "spend_date"));
          se.setCurrency(CurrencyValues.valueOf(rs.getString( "currency")));
          se.setAmount(rs.getDouble( "amount"));
          se.setDescription(rs.getString( "description"));
          return Optional.of(se);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void remove(SpendEntity spend ) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"spend\" WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
            "DELETE FROM \"category\" WHERE id = ?"
    )) {
      ps.setObject(1, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
