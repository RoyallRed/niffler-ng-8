package guru.qa.niffler.data.repository.impl.spendRepository;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;

import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();
    private final String url = CFG.spendJdbcUrl();
    private final SpendDao spendDao = new SpendDaoSpringJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
       return spendDao.create(spend);
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return spendDao.createCategory(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return spendDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        return spendDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDao.findById(id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendingDescription(String username, String description) {
        return spendDao.findByUsernameAndSpendingDescription(username,description);
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.remove(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        spendDao.removeCategory(category);
    }


}