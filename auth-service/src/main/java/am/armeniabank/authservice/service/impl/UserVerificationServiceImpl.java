package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.cilent.AuditClient;
import am.armeniabank.authservice.dto.ApproveVerificationRequestDto;
import am.armeniabank.authservice.dto.AuditEventDto;
import am.armeniabank.authservice.dto.RejectVerificationRequestDto;
import am.armeniabank.authservice.dto.StartVerificationRequestDto;
import am.armeniabank.authservice.dto.UploadDocumentRequestDto;
import am.armeniabank.authservice.dto.UserVerificationResponseDto;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.DocumentType;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerificationMethod;
import am.armeniabank.authservice.entity.emuns.VerificationStatus;
import am.armeniabank.authservice.entity.emuns.VerifierType;
import am.armeniabank.authservice.mapper.UserVerificationMapper;
import am.armeniabank.authservice.repository.UserVerificationRepository;
import am.armeniabank.authservice.service.UserVerificationService;
import am.armeniabank.authservice.util.ImageUtil;
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
    public UserVerificationResponseDto startVerification(UUID userId, StartVerificationRequestDto requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);
        userVerification.setPassportNumber(requestDto.getPassportNumber());
        userVerification.setBankAccountNumber(requestDto.getBankAccountNumber());
        userVerification.setDocumentType(DocumentType.valueOf(requestDto.getDocumentType().name().toUpperCase(Locale.ROOT)));
        userVerification.setVerificationMethod(VerificationMethod.valueOf(requestDto.getVerificationMethod().name().toUpperCase(Locale.ROOT)));
        userVerification.setRequestedAt(LocalDateTime.now());
        userVerification.setStatus(VerificationStatus.PENDING);
        userVerification.setActive(true);

        UserVerification savedVerification = userVerificationRepository.save(userVerification);

        AuditEventDto auditEvent = new AuditEventDto(
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
    public void uploadDocuments(UUID userId, UploadDocumentRequestDto requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);

        String documentUrl = ImageUtil.uploadDocument(requestDto.getFile(), fileDocumentUrl);
        userVerification.setDocumentUrl(documentUrl);
        userVerification.setDocumentsUploadedAt(LocalDateTime.now());

        userVerificationRepository.save(userVerification);

        AuditEventDto auditEvent = new AuditEventDto(
                "auth-service",
                "DOCUMENT_VERIFICATION",
                "User Document Verification with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);
    }

    @Override
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void approveVerification(UUID userId, ApproveVerificationRequestDto requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);

        userVerification.setVerifiedBy(requestDto.getVerifier());
        userVerification.setVerifiedByType(VerifierType.valueOf(requestDto.getVerifierType().name().toUpperCase(Locale.ROOT)));
        userVerification.setVerifiedAt(LocalDateTime.now());
        userVerification.setStatus(VerificationStatus.VERIFIED);
        userVerification.setActive(false);

        userVerificationRepository.save(userVerification);

        AuditEventDto auditEvent = new AuditEventDto(
                "auth-service",
                "VERIFIER_TYPE",
                "User Verifier Type Verification with username: " + userVerification.getUser().getEmail(),
                LocalDateTime.now()
        );

        auditClient.sendAuditEvent(auditEvent);

    }

    @Override
    @CacheEvict(value = "userVerificationById", key = "#userId")
    public void rejectVerification(UUID userId, RejectVerificationRequestDto requestDto) {

        UserVerification userVerification = findUserVerificationById(userId);

        userVerification.setRejectionReason(RejectionReason.valueOf(requestDto.getRejectionReason().name().toUpperCase(Locale.ROOT)));
        userVerification.setAdditionalComments(requestDto.getComment());
        userVerification.setStatus(VerificationStatus.REJECTED);
        userVerification.setActive(false);

        userVerificationRepository.save(userVerification);

        AuditEventDto auditEvent = new AuditEventDto(
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

        AuditEventDto auditEvent = new AuditEventDto(
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
    public UserVerificationResponseDto getVerificationStatus(UUID userId) {
        UserVerification userVerification = findUserVerificationById(userId);
        return userVerificationMapper.toUserProfileDto(userVerification);
    }

    private UserVerification findUserVerificationById(UUID userId) {
        return userVerificationRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + userId + " not found"));
    }
}
