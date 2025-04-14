package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity implements Serializable {

    private UUID id;

    private Authority authority;

    private AuthUserEntity user;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setId(json.id());
        ae.setAuthority(json.authority());
        ae.setUser(AuthUserEntity.fromJson(json.user()));
        return ae;
    }

}