package am.armeniabank.authservice.controller;

import am.armeniabank.authservice.dto.ApproveVerificationRequestDto;
import am.armeniabank.authservice.dto.RejectVerificationRequestDto;
import am.armeniabank.authservice.dto.StartVerificationRequestDto;
import am.armeniabank.authservice.dto.UploadDocumentRequestDto;
import am.armeniabank.authservice.dto.UserVerificationResponseDto;
import am.armeniabank.authservice.exception.custom.UserServerError;
import am.armeniabank.authservice.service.UserVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/verification")
@Validated
@Slf4j
public class UserVerificationController {

    private final UserVerificationService userVerificationService;

    @PutMapping("/start/{userId}")
    public ResponseEntity<UserVerificationResponseDto> startVerification(@PathVariable("userId") UUID userId,
                                                                         @Valid @RequestBody StartVerificationRequestDto requestDto) {

        try {
            UserVerificationResponseDto startVerification = userVerificationService.startVerification(userId, requestDto);
            return ResponseEntity.ok(startVerification);
        } catch (UserServerError e) {
            log.error("UserServerError during verification start for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            log.error("Unexpected error during verification start for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping(value = "/update/document/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadDocuments(@PathVariable("userId") UUID userId,
                                                               @RequestPart("file") MultipartFile file) {
        try {
            UploadDocumentRequestDto requestDto = new UploadDocumentRequestDto();
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

    @PutMapping("/approve/{userId}")
    public ResponseEntity<Map<String, String>> approveVerification(@PathVariable("userId") UUID userId,
                                                                   @Valid @RequestBody ApproveVerificationRequestDto requestDto) {
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

    @PutMapping("/reject/{userId}")
    public ResponseEntity<Map<String, String>> rejectVerification(@PathVariable("userId") UUID userId,
                                                                  @Valid @RequestBody RejectVerificationRequestDto requestDto) {
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

    @PutMapping("/expire/{userId}")
    public ResponseEntity<Map<String, String>> expireVerification(@PathVariable("userId") UUID userId) {
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

    @GetMapping("/{userId}")
    public ResponseEntity<UserVerificationResponseDto> getVerificationStatus(@PathVariable("userId") UUID userId) {
        try {
            UserVerificationResponseDto status = userVerificationService.getVerificationStatus(userId);
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
