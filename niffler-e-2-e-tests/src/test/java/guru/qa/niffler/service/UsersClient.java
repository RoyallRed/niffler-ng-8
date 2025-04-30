package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {

     UserJson createUser(UserJson user);

     UserJson createUser(String username, String password);

     void addFriend(UserJson user, UserJson friend);

     void addIncomeInvitation(UserJson targertUser, int count);

     void addOutcomeInvitation(UserJson targetUser, int count);
}