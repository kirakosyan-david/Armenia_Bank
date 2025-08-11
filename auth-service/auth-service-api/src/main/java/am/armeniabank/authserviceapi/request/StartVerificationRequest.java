package am.armeniabank.authserviceapi.request;

import am.armeniabank.authserviceapi.emuns.DocumentType;
import am.armeniabank.authserviceapi.emuns.VerificationMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartVerificationRequest {

    @NotBlank(message = "Passport number must not be blank")
    private String passportNumber;

    @NotBlank(message = "Bank account number must not be blank")
    private String bankAccountNumber;

    @NotNull(message = "Document type must be provided")
    private DocumentType documentType;

    @NotNull(message = "Verification method must be provided")
    private VerificationMethod verificationMethod;
}
