package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerifierType;
import am.armeniabank.authservice.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVerificationServiceImpl implements UserVerificationService {

    @Override
    public UserVerification startVerification(UUID userId, UserVerification verificationData) {
        return null;
    }

    @Override
    public void uploadDocuments(UUID userId, String documentUrl) {

    }

    @Override
    public void approveVerification(UUID userId, String verifier, VerifierType type) {

    }

    @Override
    public void rejectVerification(UUID userId, RejectionReason reason, String comment) {

    }

    @Override
    public void expireVerification(UUID userId) {

    }

    @Override
    public UserVerification getVerificationStatus(UUID userId) {
        return null;
    }
}
