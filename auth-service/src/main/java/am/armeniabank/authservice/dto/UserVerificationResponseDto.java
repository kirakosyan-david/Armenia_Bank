package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.DocumentType;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerificationMethod;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import am.armeniabank.authservice.entity.emuns.VerifierType;
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
public class UserVerificationResponseDto {

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
