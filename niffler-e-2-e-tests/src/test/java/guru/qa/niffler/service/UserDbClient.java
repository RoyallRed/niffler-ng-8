package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.user.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.UserJson;

import static guru.qa.niffler.data.Databases.xaTransaction;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();

    public UserJson createUserAuthAndUserdata(AuthUserJson authUser, UserJson userdataUser) {

        Databases.XaFunction<UserJson> xaFunAuthUser = new Databases.XaFunction<>(
                connection -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUser);
                    new AuthUserDaoJdbc(connection).createUser(authUserEntity);

                    for (var authority : authUserEntity.getAuthorities()) {
                        new AuthAuthorityDaoJdbc(connection).createAuthoritiy(authority);
                    }

                    return UserJson.fromAuthUserEntity(authUserEntity);
                },
                CFG.authJdbcUrl()
        );


        Databases.XaFunction<UserJson> xaFunUserData = new Databases.XaFunction<>(
                connection -> {
                    UserEntity userEntity = UserEntity.fromJson(userdataUser);
                    return UserJson.fromUserEntity(new UserdataUserDaoJdbc(connection).createUser(userEntity));
                },
                CFG.userdataJdbcUrl()
        );
        return xaTransaction(TRANSACTION_READ_COMMITTED, xaFunAuthUser, xaFunUserData);
    }

}
