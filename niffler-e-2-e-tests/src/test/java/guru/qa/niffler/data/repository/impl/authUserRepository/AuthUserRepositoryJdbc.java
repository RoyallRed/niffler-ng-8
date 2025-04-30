package guru.qa.niffler.data.repository.impl.authUserRepository;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

  private static final Config CFG = Config.getInstance();
  private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

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
  return   authUserDao.update(user);
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    return authUserDao.findById(id);
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    return authUserDao.findByUsername(username);
  }

  @Override
  public void remove(AuthUserEntity user) {
    authUserDao.remove(user);
  }
}
