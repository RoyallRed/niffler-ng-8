package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.user.AuthorityEntity;

public interface AuthAuthorityDao {

    AuthorityEntity createPermission(AuthorityEntity user);
}
