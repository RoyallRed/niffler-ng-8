package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UdUserEntityExtractor implements ResultSetExtractor<UserEntity> {
    public static final UdUserEntityExtractor instance = new UdUserEntityExtractor();

    public UdUserEntityExtractor() {
    }

    @Override
    public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserEntity> userCache = new ConcurrentHashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("user_id", UUID.class);
            UserEntity user = userCache.get(userId);
            if (user == null) {
                UserEntity newUser = new UserEntity();
                newUser.setId(rs.getObject("user_id", UUID.class));
                newUser.setUsername(rs.getString("username"));
                newUser.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                newUser.setFirstname(rs.getString("firstname"));
                newUser.setSurname(rs.getString("surname"));
                newUser.setPhoto(rs.getBytes("photo"));
                newUser.setPhotoSmall(rs.getBytes("photo_small"));
                userCache.put(userId, newUser);
                user = newUser;
            }

            FriendshipEntity friendshipEntity = new FriendshipEntity();
            friendshipEntity.setRequester(new UserEntity(rs.getObject("requester_id", UUID.class)));
            friendshipEntity.setAddressee(new UserEntity(rs.getObject("addressee_id", UUID.class)));
            friendshipEntity.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
            friendshipEntity.setCreatedDate(rs.getDate("created_date"));

            if (userId.equals(friendshipEntity.getAddressee().getId())) {
                user.getFriendshipAddressees().add(friendshipEntity);
            } else if (userId.equals(friendshipEntity.getRequester().getId())) {
                user.getFriendshipRequests().add(friendshipEntity);
            }
        }
        return userCache.get(userId);
    }
}
