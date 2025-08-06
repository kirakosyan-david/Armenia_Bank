package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.ApproveVerificationRequestDto;
import am.armeniabank.authservice.dto.RejectVerificationRequestDto;
import am.armeniabank.authservice.dto.StartVerificationRequestDto;
import am.armeniabank.authservice.dto.UploadDocumentRequestDto;
import am.armeniabank.authservice.dto.UserVerificationResponseDto;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.RejectionReason;
import am.armeniabank.authservice.entity.emuns.VerifierType;

import java.util.UUID;

public interface UserVerificationService {

    UserVerificationResponseDto startVerification(UUID userId, StartVerificationRequestDto requestDto);

    void uploadDocuments(UUID userId, UploadDocumentRequestDto requestDto);

    void approveVerification(UUID userId, ApproveVerificationRequestDto requestDto);

    void rejectVerification(UUID userId, RejectVerificationRequestDto requestDto);

    void expireVerification(UUID userId);

    void expireAllOutdatedVerifications();

    UserVerificationResponseDto getVerificationStatus(UUID userId);

}