package guru.qa.niffler.data.repository.impl.userdataUserRepository;

import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    @Override
    public UserEntity create(UserEntity user) {
        return udUserDao.create(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return udUserDao.findById(id) ;
    }

    public Optional<UserEntity> findByUsername(String username) {
        return udUserDao.findByUsername(username);
    }
    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        udUserDao.sendInvitation(addressee,requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        udUserDao.addFriend(requester,addressee);
    }
}
