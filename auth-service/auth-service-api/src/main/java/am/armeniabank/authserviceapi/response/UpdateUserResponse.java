package am.armeniabank.authserviceapi.response;

import am.armeniabank.authserviceapi.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {

    private String email;

    private UserRole role;

    private Boolean emailVerified;

    private String passportNumber;
}