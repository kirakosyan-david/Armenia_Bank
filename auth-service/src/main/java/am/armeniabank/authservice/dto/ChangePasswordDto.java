package am.armeniabank.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8)
    private String newPassword;
}