package am.armeniabank.templateservice.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    private String email;

    private String password;

    private String passportNumber;

    private boolean emailVerified;

    private String firstName;

    private String lastName;

    private String patronymic;

}