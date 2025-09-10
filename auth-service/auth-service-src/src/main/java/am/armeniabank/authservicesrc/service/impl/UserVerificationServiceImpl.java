package am.armeniabank.authservicesrc.service.impl;

import am.armeniabank.authserviceapi.emuns.DocumentType;
import am.armeniabank.authserviceapi.emuns.RejectionReason;
import am.armeniabank.authserviceapi.emuns.VerificationMethod;
import am.armeniabank.authserviceapi.emuns.VerificationStatus;
import am.armeniabank.authserviceapi.emuns.VerifierType;
import am.armeniabank.authserviceapi.request.ApproveVerificationRequest;
import am.armeniabank.authserviceapi.request.RejectVerificationRequest;
import am.armeniabank.authserviceapi.request.StartVerificationRequest;
import am.armeniabank.authserviceapi.request.UploadDocumentRequest;
import am.armeniabank.authserviceapi.response.UserVerificationResponse;
import am.armeniabank.authservicesrc.entity.UserVerification;
import am.armeniabank.authservicesrc.exception.custom.UserVerificationException;
import am.armeniabank.authservicesrc.integration.AuditServiceClient;
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authservicesrc.mapper.UserVerificationMapper;
import am.armeniabank.authservicesrc.repository.UserVerificationRepository;
import am.armeniabank.authservicesrc.service.UserVerificationService;
import am.armeniabank.authservicesrc.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVerificationServiceImpl implements UserVerificationService {

    private final UserVerificationRepository userVerificationRepository;
    private final UserVerificationMapper userVerificationMapper;
    private final AuditServiceClient auditClient;

    @Value("${user.verification.picture.path}")
    private String fileDocumentUrl;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Cacheable(value = "userVerificationById", key = "#userId")
    public UserVerificationResponse startVerification(UUID userId, StartVerificationRequest requestDto) {
        try {
            log.info("Starting verification for userId={}", userId);

            UserVerification userVerification = findUserVerificationById(userId);

            userVerification.setPassportNumber(requestDto.getPassportNumber());
            userVerification.setBankAccountNumber(requestDto.getBankAccountNumber());
            userVerification.setDocumentType(DocumentType.valueOf(requestDto.getDocumentType().name().toUpperCase(Locale.ROOT)));
            userVerification.setVerificationMethod(VerificationMethod.valueOf(requestDto.getVerificationMethod().name().toUpperCase(Locale.ROOT)));
            userVerification.setRequestedAt(LocalDateTime.now());
            userVerification.setStatus(VerificationStatus.PENDING);
            userVerification.setActive(true);

            UserVerification savedVerification = userVerificationRepository.save(userVerification);

            sendAuditEvent("START_VERIFICATION",
                    "User start verification with email: " + userVerification.getUser().getEmail());

            log.info("Verification started successfully for userId={}", userId);

            return userVerificationMapper.toUserProfileDto(savedVerification);
        } catch (Exception e) {
            log.error("Failed to start verification for userId={}: {}", userId, e.getMessage(), e);
            throw new UserVerificationException("Failed to start verification for userId=" + userId, e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void uploadDocuments(UUID userId, UploadDocumentRequest requestDto) {
        try {
            log.info("Uploading documents for userId={}", userId);
            UserVerification userVerification = findUserVerificationById(userId);

            String documentUrl = ImageUtil.uploadDocument(requestDto.getFile(), fileDocumentUrl);
            userVerification.setDocumentUrl(documentUrl);
            userVerification.setDocumentsUploadedAt(LocalDateTime.now());

            userVerificationRepository.save(userVerification);

            sendAuditEvent("DOCUMENT_VERIFICATION", "User document uploaded for email: " + userVerification.getUser().getEmail());

            log.info("Documents uploaded successfully for userId={}", userId);
            ;
        } catch (Exception e) {
            log.error("Failed to upload documents for userId={}: {}", userId, e.getMessage(), e);
            throw new UserVerificationException("Failed to start verification for userId=" + userId, e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void approveVerification(UUID userId, ApproveVerificationRequest requestDto) {
        try {
            log.info("Approving verification for userId={}", userId);
            UserVerification userVerification = findUserVerificationById(userId);

            userVerification.setVerifiedBy(requestDto.getVerifier());
            userVerification.setVerifiedByType(VerifierType.valueOf(requestDto.getVerifierType().name().toUpperCase(Locale.ROOT)));
            userVerification.setVerifiedAt(LocalDateTime.now());
            userVerification.setStatus(VerificationStatus.VERIFIED);
            userVerification.setActive(false);

            userVerificationRepository.save(userVerification);
            sendAuditEvent("VERIFIER_TYPE", "Verification approved for email: " + userVerification.getUser().getEmail());

            log.info("Verification approved for userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to approve verification for userId={}: {}", userId, e.getMessage(), e);
            throw new UserVerificationException("Failed to start verification for userId=" + userId, e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void rejectVerification(UUID userId, RejectVerificationRequest requestDto) {
        try {
            log.info("Rejecting verification for userId={}", userId);

            UserVerification userVerification = findUserVerificationById(userId);

            userVerification.setRejectionReason(RejectionReason.valueOf(requestDto.getRejectionReason().name().toUpperCase(Locale.ROOT)));
            userVerification.setAdditionalComments(requestDto.getComment());
            userVerification.setStatus(VerificationStatus.REJECTED);
            userVerification.setActive(false);

            userVerificationRepository.save(userVerification);
            sendAuditEvent("REJECTION_REASON", "Verification rejected for email: " + userVerification.getUser().getEmail());

            log.info("Verification rejected for userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to reject verification for userId={}: {}", userId, e.getMessage(), e);
            throw new UserVerificationException("Failed to start verification for userId=" + userId, e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void expireVerification(UUID userId) {
        try {
            log.info("Expiring verification for userId={}", userId);

            UserVerification userVerification = findUserVerificationById(userId);

            if (userVerification.getStatus() == VerificationStatus.VERIFIED ||
                    userVerification.getStatus() == VerificationStatus.EXPIRED) {
                log.info("Verification already completed or expired for userId={}", userId);
                return;
            }
            userVerification.setExpiredAt(LocalDateTime.now());
            userVerification.setStatus(VerificationStatus.EXPIRED);
            userVerification.setActive(false);

            userVerificationRepository.save(userVerification);
            sendAuditEvent("VERIFICATION_EXPIRED", "Verification expired for email: " + userVerification.getUser().getEmail());

            log.info("Verification expired for userId={}", userId);
        } catch (Exception e) {
            log.error("Failed to expire verification for userId={}: {}", userId, e.getMessage(), e);
            throw new UserVerificationException("Failed to start verification for userId=" + userId, e);
        }
    }

    public void expireAllOutdatedVerifications() {
        List<UserVerification> outdatedVerifications = userVerificationRepository
                .findByStatusAndActive(VerificationStatus.PENDING, true);

        LocalDateTime now = LocalDateTime.now();

        for (UserVerification verification : outdatedVerifications) {
            if (verification.getRequestedAt().plusHours(24).isBefore(now)) {
                expireVerification(verification.getUserId());
            }
        }
    }

    @Override
    public UserVerificationResponse getVerificationStatus(UUID userId) {
        try {
            log.info("Fetching verification status for userId={}", userId);
            UserVerification userVerification = findUserVerificationById(userId);
            return userVerificationMapper.toUserProfileDto(userVerification);
        } catch (Exception e) {
            log.error("Failed to fetch verification status for userId={}: {}", userId, e.getMessage(), e);
            throw new UserVerificationException("Failed to start verification for userId=" + userId, e);
        }

    }

    private UserVerification findUserVerificationById(UUID userId) {
        return userVerificationRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + userId + " not found"));
    }

    private void sendAuditEvent(String action, String message) {
        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                action,
                message,
                LocalDateTime.now()
        );
        auditClient.sendAuditEvent(auditEvent);
    }

}
