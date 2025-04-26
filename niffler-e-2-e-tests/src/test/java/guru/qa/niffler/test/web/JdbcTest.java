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
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUser(
        new UserJson(
            null,
            "valentin-4",
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
  }


  @Test
  void createFriendshipTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user1 = usersDbClient.createUser(
            new UserJson(
                    null,
                    "valentin-15",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );

    UserJson user2 = usersDbClient.createUser(
            new UserJson(
                    null,
                    "valentin-16",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );
    usersDbClient.addFriendship(user1,user2);
  }

  @Test
  void createFriendRequestTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user1 = usersDbClient.createUser(
            new UserJson(
                    null,
                    "valentin-17",
                    null,
                    null,
                    null,
                    CurrencyValues.RUB,
                    null,
                    null,
                    null
            )
    );

    UserJson user2 = usersDbClient.createUser(
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
    usersDbClient.addFriendRequest(user1,user2);
  }



}
