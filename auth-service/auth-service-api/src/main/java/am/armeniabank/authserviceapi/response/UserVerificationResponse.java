package am.armeniabank.authserviceapi.response;

import am.armeniabank.authserviceapi.emuns.DocumentType;
import am.armeniabank.authserviceapi.emuns.RejectionReason;
import am.armeniabank.authserviceapi.emuns.VerificationMethod;
import am.armeniabank.authserviceapi.emuns.VerificationStatus;
import am.armeniabank.authserviceapi.emuns.VerifierType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationResponse {

    private UUID userId;

    private VerificationStatus status;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "UTC")
    private LocalDateTime requestedAt;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "UTC")
    private LocalDateTime verifiedAt;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "UTC")
    private LocalDateTime expiredAt;

    private String passportNumber;

    private String bankAccountNumber;

    private String documentUrl;

    private DocumentType documentType;

    private RejectionReason rejectionReason;

    private VerificationMethod verificationMethod;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "UTC")
    private LocalDateTime documentsUploadedAt;

    private String verifiedBy;

    private VerifierType verifiedByType;

    private boolean active;

    private String additionalComments;
}
