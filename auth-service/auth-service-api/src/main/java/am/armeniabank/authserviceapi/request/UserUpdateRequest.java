package am.armeniabank.authserviceapi.request;

import am.armeniabank.authserviceapi.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    private String email;

    private String password;

    private UserRole role;

    private boolean emailVerified;

}
