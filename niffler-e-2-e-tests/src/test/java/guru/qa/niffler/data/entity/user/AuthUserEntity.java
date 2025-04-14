package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.model.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
public class AuthUserEntity  implements Serializable {
    private UUID id;

    private String username;

    private String password;

    private Boolean enabled;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;


    private List<AuthorityEntity> authorities = new ArrayList<>();

    public void addAuthorities(AuthorityEntity... authorities) {
        for (AuthorityEntity permission : authorities) {
            this.authorities.add(permission);
            permission.setUser(this);
        }
    }

    public static AuthUserEntity fromJson(AuthUserJson json){
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setId(json.id());
        authUserEntity.setUsername(json.username());
        authUserEntity.setPassword(json.password());
        authUserEntity.setEnabled(json.enabled());
        authUserEntity.setAccountNonExpired(json.accountNonExpired());
        authUserEntity.setAccountNonLocked(json.accountNonLocked());
        authUserEntity.setCredentialsNonExpired(json.credentialsNonExpired());

        AuthorityEntity readPermissionEntity = new AuthorityEntity();
        readPermissionEntity.setAuthority(Authority.read);
        AuthorityEntity writePermissionEntity = new AuthorityEntity();
        writePermissionEntity.setAuthority(Authority.write);
        authUserEntity.addAuthorities(readPermissionEntity, writePermissionEntity);


        return authUserEntity;
    }


}
