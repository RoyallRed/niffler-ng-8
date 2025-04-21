package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-2",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx",
            null
        )
    );

    System.out.println(spend);
  }

  @Test
  void springJdbcXaTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserByXaTxSpring(
        new UserJson(
            null,
            "valentin-9",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
    // транзакция успешно откатилась
  }

  @Test
  void springJdbcNoTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserNoTxSpring(
            new UserJson(
                    null,
                    "valentin-10",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
    // транзакция не откатилась
  }

  @Test
  void JdbcNoTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserNoTxJdbc(
            new UserJson(
                    null,
                    "valentin-14",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
    // транзакция не откатилась
  }
  @Test
  void JdbcXaTxTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserByXaTxJdbc(
            new UserJson(
                    null,
                    "valentin-18",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
    // транзакция успешно откатилась
  }

  @Test
  void chainedTransactionTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUserChainedTx(
            new UserJson(
                    null,
                    "valentin-12",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    System.out.println(user);
    // транзакция не откатилась. Нарушен принцип ACID. Поэтому ChainedTransactionManager стал Deprecated
  }


}
