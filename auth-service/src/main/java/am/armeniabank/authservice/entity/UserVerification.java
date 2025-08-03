package am.armeniabank.authservice.entity;

import am.armeniabank.authservice.entity.emuns.DocumentType;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerificationMethod;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import am.armeniabank.authservice.entity.emuns.VerifierType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "user_verifications")
public class UserVerification {

    @Id
    @Column(name = "user_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    private LocalDateTime requestedAt;

    private LocalDateTime verifiedAt;

    private LocalDateTime expiredAt;

    @Column(nullable = false, unique = true)
    private String passportNumber;

    private String bankAccountNumber;

    private String documentUrl;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    private RejectionReason rejectionReason;

    @Enumerated(EnumType.STRING)
    private VerificationMethod verificationMethod;

    private LocalDateTime documentsUploadedAt;

    @Column(name = "verified_by")
    private String verifiedBy;

    @Column(name = "verified_by_type")
    @Enumerated(EnumType.STRING)
    private VerifierType verifiedByType;

    private boolean active;

    @Column(name = "additional_comments", length = 1000)
    private String additionalComments;

    @Column(name = "verification_token", length = 64)
    private String verificationToken;

    private LocalDateTime verificationTokenExpiry;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
}
