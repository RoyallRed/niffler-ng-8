package guru.qa.niffler.data.repository.impl.authUserRepository;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();
  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    authUserDao.create(user);
    for (AuthorityEntity ae :user.getAuthorities()) {
      authAuthorityDao.create(ae);
    }
    return user;
  }

  @Override
  public AuthUserEntity update(AuthUserEntity user) {
   return authUserDao.update(user);

  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
   return  authUserDao.findById(id);
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    return  authUserDao.findByUsername(username);
  }

  @Override
  public void remove(AuthUserEntity user) {
    authUserDao.remove(user);
  }
}
