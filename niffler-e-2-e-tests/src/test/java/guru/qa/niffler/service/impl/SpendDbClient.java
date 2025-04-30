package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.spendRepository.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;


public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();


  private final SpendRepository spendRepository = new SpendRepositoryHibernate();

  private final CategoryDao categoryDao = new CategoryDaoJdbc();
  private final SpendDao spendDao = new SpendDaoJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  public SpendJson createSpend(SpendJson spend) {
      return jdbcTxTemplate.execute(() -> {
                  SpendEntity spendEntity = SpendEntity.fromJson(spend);
                  return SpendJson.fromEntity(
                          spendRepository.create(spendEntity));
              }
      );
        }
  }

