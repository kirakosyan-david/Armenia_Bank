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
import am.armeniabank.authservicesrc.kafka.model.AuditEvent;
import am.armeniabank.authserviceapi.response.UserVerificationResponse;
import am.armeniabank.authservicesrc.cilent.AuditClient;
import am.armeniabank.authservicesrc.entity.UserVerification;
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
    private final AuditClient auditClient;

    @Value("${user.verification.picture.path}")
    private String fileDocumentUrl;

    @Override
    @Cacheable(value = "userVerificationById", key = "#userId")
    public UserVerificationResponse startVerification(UUID userId, StartVerificationRequest requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);
        userVerification.setPassportNumber(requestDto.getPassportNumber());
        userVerification.setBankAccountNumber(requestDto.getBankAccountNumber());
        userVerification.setDocumentType(DocumentType.valueOf(requestDto.getDocumentType().name().toUpperCase(Locale.ROOT)));
        userVerification.setVerificationMethod(VerificationMethod.valueOf(requestDto.getVerificationMethod().name().toUpperCase(Locale.ROOT)));
        userVerification.setRequestedAt(LocalDateTime.now());
        userVerification.setStatus(VerificationStatus.PENDING);
        userVerification.setActive(true);

        UserVerification savedVerification = userVerificationRepository.save(userVerification);

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "START_VERIFICATION",
                "User start Verification with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

        return userVerificationMapper.toUserProfileDto(savedVerification);
    }

    @Override
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void uploadDocuments(UUID userId, UploadDocumentRequest requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);

        String documentUrl = ImageUtil.uploadDocument(requestDto.getFile(), fileDocumentUrl);
        userVerification.setDocumentUrl(documentUrl);
        userVerification.setDocumentsUploadedAt(LocalDateTime.now());

        userVerificationRepository.save(userVerification);

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "DOCUMENT_VERIFICATION",
                "User Document Verification with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);
    }

    @Override
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void approveVerification(UUID userId, ApproveVerificationRequest requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);

        userVerification.setVerifiedBy(requestDto.getVerifier());
        userVerification.setVerifiedByType(VerifierType.valueOf(requestDto.getVerifierType().name().toUpperCase(Locale.ROOT)));
        userVerification.setVerifiedAt(LocalDateTime.now());
        userVerification.setStatus(VerificationStatus.VERIFIED);
        userVerification.setActive(false);

        userVerificationRepository.save(userVerification);

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "VERIFIER_TYPE",
                "User Verifier Type Verification with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

    }

    @Override
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void rejectVerification(UUID userId, RejectVerificationRequest requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);

        userVerification.setRejectionReason(RejectionReason.valueOf(requestDto.getRejectionReason().name().toUpperCase(Locale.ROOT)));
        userVerification.setAdditionalComments(requestDto.getComment());
        userVerification.setStatus(VerificationStatus.REJECTED);
        userVerification.setActive(false);

        userVerificationRepository.save(userVerification);

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "REJECTION_REASON",
                "User Rejection Reason Verification with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

    }

    @Override
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void expireVerification(UUID userId) {
        UserVerification userVerification = findUserVerificationById(userId);

        if (userVerification.getStatus() == VerificationStatus.VERIFIED ||
                userVerification.getStatus() == VerificationStatus.EXPIRED) {
            return;
        }

        userVerification.setExpiredAt(LocalDateTime.now());
        userVerification.setStatus(VerificationStatus.EXPIRED);
        userVerification.setActive(false);

        userVerificationRepository.save(userVerification);

        AuditEvent auditEvent = new AuditEvent(
                "auth-service",
                "VERIFICATION_EXPIRED",
                "User Verification expire with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);
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
        UserVerification userVerification = findUserVerificationById(userId);
        return userVerificationMapper.toUserProfileDto(userVerification);
    }

    private UserVerification findUserVerificationById(UUID userId) {
        return userVerificationRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + userId + " not found"));
    }
}
