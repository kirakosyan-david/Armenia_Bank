package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.UserRoles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String email;
    private String passportNumber;
    private boolean emailVerified;
    private boolean enabled;
    private UserRoles role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
