package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.DocumentType;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.entity.emuns.VerificationMethod;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import am.armeniabank.authservice.entity.emuns.VerifierType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEmailSearchResponseDto {

    private UUID id;

    private String email;

    private UserRole role;

    private boolean enabled;

    private boolean emailVerified;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime accountLockedUntil;

    private String firstName;

    private String lastName;

    private String patronymic;

    private VerificationStatus status;

    private String passportNumber;

    private LocalDateTime requestedAt;

    private LocalDateTime verifiedAt;

    private LocalDateTime expiredAt;

    private String bankAccountNumber;

    private String documentUrl;

    private DocumentType documentType;

    private RejectionReason rejectionReason;

    private VerificationMethod verificationMethod;

    private LocalDateTime documentsUploadedAt;

    private String verifiedBy;

    private VerifierType verifiedByType;

    private boolean active;

    private String additionalComments;
}
