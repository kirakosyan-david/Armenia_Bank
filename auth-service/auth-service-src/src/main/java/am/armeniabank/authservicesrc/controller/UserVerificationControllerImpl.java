package am.armeniabank.authservicesrc.controller;

import am.armeniabank.authserviceapi.contract.UserVerificationController;
import am.armeniabank.authserviceapi.request.ApproveVerificationRequest;
import am.armeniabank.authserviceapi.request.RejectVerificationRequest;
import am.armeniabank.authserviceapi.request.StartVerificationRequest;
import am.armeniabank.authserviceapi.request.UploadDocumentRequest;
import am.armeniabank.authserviceapi.response.UserVerificationResponse;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserVerificationControllerImpl implements UserVerificationController {

    private final UserVerificationService userVerificationService;

    @Override
    public ResponseEntity<UserVerificationResponse> startVerification(UUID userId, StartVerificationRequest requestDto) {

        try {
            UserVerificationResponse startVerification = userVerificationService.startVerification(userId, requestDto);
            return ResponseEntity.ok(startVerification);
        } catch (UserServerError e) {
            log.error("UserServerError during verification start for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("Unexpected error during verification start for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> uploadDocuments(UUID userId, MultipartFile file) {
        try {
            UploadDocumentRequest requestDto = new UploadDocumentRequest();
            requestDto.setFile(file);
            userVerificationService.uploadDocuments(userId, requestDto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Document uploaded successfully"));
        } catch (UserServerError e) {
            log.error("UserServerError during document upload for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        } catch (Exception e) {
            log.error("Unexpected error during document upload for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> approveVerification(UUID userId, ApproveVerificationRequest requestDto) {
        try {
            userVerificationService.approveVerification(userId, requestDto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Verification approved"));
        } catch (UserServerError e) {
            log.error("UserServerError during verification approval for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        } catch (Exception e) {
            log.error("Unexpected error during verification approval for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> rejectVerification(UUID userId, RejectVerificationRequest requestDto) {
        try {
            userVerificationService.rejectVerification(userId, requestDto);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Verification rejected"));
        } catch (UserServerError e) {
            log.error("UserServerError during verification rejection for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        } catch (Exception e) {
            log.error("Unexpected error during verification rejection for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> expireVerification(UUID userId) {
        try {
            userVerificationService.expireVerification(userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Verification expired"));
        } catch (UserServerError e) {
            log.error("UserServerError during verification expiration for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        } catch (Exception e) {
            log.error("Unexpected error during verification expiration for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Internal server error"));
        }
    }

    @Override
    public ResponseEntity<UserVerificationResponse> getVerificationStatus(UUID userId) {
        try {
            UserVerificationResponse status = userVerificationService.getVerificationStatus(userId);
            return ResponseEntity.ok(status);
        } catch (UserServerError e) {
            log.error("UserServerError during verification status retrieval for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            log.error("Unexpected error during verification status retrieval for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
