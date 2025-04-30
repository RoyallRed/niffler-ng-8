package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {

  SpendEntity create(SpendEntity spend);

  List<SpendEntity> findAll();
  SpendEntity update(SpendEntity spend);

  CategoryEntity createCategory(CategoryEntity category);

  Optional<CategoryEntity> findCategoryById(UUID id);

  Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name);

  Optional<SpendEntity> findById(UUID id);

  Optional<SpendEntity> findByUsernameAndSpendingDescription(String username, String description);

  void remove(SpendEntity spend);

  void removeCategory(CategoryEntity category);
}
