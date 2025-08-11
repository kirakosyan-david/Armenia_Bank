package am.armeniabank.authserviceapi.request;

import am.armeniabank.authserviceapi.emuns.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileUpdateRequest {

    private String firstName;

    private String lastName;

    private String patronymic;

    @JsonFormat(pattern = "dd.MM.yyyy", timezone = "UTC")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String phoneNumber;

    private String address;

    private String nationality;

    private String citizenship;

    private String timezone;

    private String preferredLanguage;
}
