package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
