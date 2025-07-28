package am.armeniabank.authservice.service;

import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerifierType;

import java.util.UUID;

public interface UserVerificationService {

    UserVerification startVerification(UUID userId, UserVerification verificationData);

    void uploadDocuments(UUID userId, String documentUrl);

    void approveVerification(UUID userId, String verifier, VerifierType type);

    void rejectVerification(UUID userId, RejectionReason reason, String comment);

    void expireVerification(UUID userId);

    UserVerification getVerificationStatus(UUID userId);

}