package am.armeniabank.authservicesrc.controller;

import am.armeniabank.authserviceapi.contract.UserController;
import am.armeniabank.authserviceapi.request.UserUpdateRequest;
import am.armeniabank.authserviceapi.response.UpdateUserResponse;
import am.armeniabank.authserviceapi.response.UserEmailSearchResponse;
import am.armeniabank.authserviceapi.response.UserResponse;
import am.armeniabank.authservicesrc.exception.custom.SearchEmailException;
import am.armeniabank.authservicesrc.exception.custom.UserServerError;
import am.armeniabank.authservicesrc.exception.custom.WrongUserIdException;
import am.armeniabank.authservicesrc.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserEmailSearchResponse> searchEmail(String email) {
        try {
            UserEmailSearchResponse dto = userService.searchByEmail(email);
            return ResponseEntity.ok(dto);
        } catch (SearchEmailException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UserServerError e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<UserResponse> findUserById(UUID id) {
        try {
            UserResponse user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (WrongUserIdException e) {
            log.error("Id failed for user {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<UpdateUserResponse> updateUser(UUID id, UserUpdateRequest request) {
        try {
            UpdateUserResponse user = userService.updateUser(id, request);
            return ResponseEntity.ok(user);
        } catch (UserServerError e) {
            log.error("Id failed for user update {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Map<String, String>> deleteUser(UUID userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted successfully"));
        } catch (UserServerError e) {
            log.error("Failed to delete user with ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to delete user"));
        }
    }

}
