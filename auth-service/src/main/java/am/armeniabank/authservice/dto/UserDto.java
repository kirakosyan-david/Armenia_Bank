package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID id;
    private String email;
    private String passportNumber;
    private boolean emailVerified;
    private UserRole role;
    private String firstName;
    private String lastName;
}
