package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {

    private String email;

    private UserRole role;

    private Boolean emailVerified;

    private String passportNumber;
}