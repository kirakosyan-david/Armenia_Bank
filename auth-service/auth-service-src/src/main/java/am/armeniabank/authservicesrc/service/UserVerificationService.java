package am.armeniabank.authservicesrc.service;

import am.armeniabank.authserviceapi.request.ApproveVerificationRequest;
import am.armeniabank.authserviceapi.request.RejectVerificationRequest;
import am.armeniabank.authserviceapi.request.StartVerificationRequest;
import am.armeniabank.authserviceapi.request.UploadDocumentRequest;
import am.armeniabank.authserviceapi.response.UserVerificationResponse;

import java.util.UUID;

public interface UserVerificationService {

    UserVerificationResponse startVerification(UUID userId, StartVerificationRequest requestDto);

    void uploadDocuments(UUID userId, UploadDocumentRequest requestDto);

    void approveVerification(UUID userId, ApproveVerificationRequest requestDto);

    void rejectVerification(UUID userId, RejectVerificationRequest requestDto);

    void expireVerification(UUID userId);

    void expireAllOutdatedVerifications();

    UserVerificationResponse getVerificationStatus(UUID userId);

}