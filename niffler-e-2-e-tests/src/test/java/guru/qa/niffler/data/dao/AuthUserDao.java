package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.user.AuthUserEntity;

public interface AuthUserDao {

    AuthUserEntity create(AuthUserEntity user);
}
