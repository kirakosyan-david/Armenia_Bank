package am.armeniabank.authserviceapi.response;

import am.armeniabank.authserviceapi.emuns.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UserProfileResponse {

    private String firstName;

    private String lastName;

    private String patronymic;

    @JsonFormat(pattern = "dd.MM.yyyy", timezone = "UTC")
    private LocalDate birthDate;

    private Gender gender;

    private String phoneNumber;

    private String address;

    private String nationality;

    private String citizenship;

    private String timezone;

    private String preferredLanguage;
}
