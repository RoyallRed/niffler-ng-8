package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

  private static final Config CFG = Config.getInstance();

  @Override
  public CategoryEntity create(CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO category (username, name, archived) " +
              "VALUES (?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      )) {
        ps.setString(1, category.getUsername());
        ps.setString(2, category.getName());
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
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT * FROM category WHERE id = ?"
      )) {
        ps.setObject(1, id);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
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
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM spend WHERE username = ? AND name = ?"
      )) {
        ps.setObject(1, username);
        ps.setObject(2, categoryName);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
          } else {
            return Optional.empty();
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }}

  @Override
  public List<CategoryEntity> findAllByUsername(String username) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "SELECT * FROM spend WHERE username = ?"
      )) {
        ps.setObject(1, username);
        ps.execute();
        try (ResultSet rs = ps.getResultSet()) {
          List<CategoryEntity> ceList = new ArrayList<>();
          while (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setUsername(rs.getString("username"));
            ce.setName(rs.getString("name"));
            ce.setArchived(rs.getBoolean("archived")); ceList.add(ce);
          }
          return ceList;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }}

  @Override
  public void deleteCategory(CategoryEntity spend) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM category WHERE id = ?"
      )) {
        ps.setObject(1, spend.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  }

