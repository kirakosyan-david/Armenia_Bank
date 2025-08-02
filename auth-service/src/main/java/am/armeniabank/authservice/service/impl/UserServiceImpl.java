package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.dto.UpdateUserDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.dto.UserResponseDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.entity.emuns.UserRole;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.security.CurrentUser;
import am.armeniabank.authservice.service.KeycloakService;
import am.armeniabank.authservice.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.account.UserRepresentation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;

    @Override
    public UserEmailSearchResponseDto searchByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User with email " + email + " not found")
        );
        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();
        return userMapper.toSearchDto(user, profile, verification);
    }

    @Override
    public UserResponseDto findById(UUID id, CurrentUser currentUser) {
        User user = findUserById(id);

        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();

        if (currentUser.getUser().getId().equals(id)) {
            return userMapper.toUserByIdDto(user, profile, verification);
        }
        throw new AccessDeniedException("You are not allowed to view this user’s data");
    }

    @Override
    public UpdateUserDto update(UUID id, UserUpdateRequest request) {

        User userById = findUserById(id);

        String oldEmail = userById.getEmail();

        boolean isEmail = keycloakService.emailExistsInKeycloak(oldEmail);
        if (isEmail) {
            log.error("User with email {} not found in Keycloak", oldEmail);
        }

        userById.setEmail(request.getEmail());
        userById.setPassword(passwordEncoder.encode(request.getPassword()));
        userById.setRole(UserRole.valueOf(request.getRole().name().toUpperCase(Locale.ROOT)));
        userById.setEmailVerified(true);
        userById.setUpdatedAt(LocalDateTime.now());

        User user = userRepository.save(userById);

        keycloakService.updateUserInKeycloak(oldEmail, request, user.getRole());

        return userMapper.toUserUpdateDto(user, user.getVerification());
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public void enableUser(UUID userId) {

    }

    @Override
    public void lockUser(UUID userId, String reason) {

    }

    @Override
    public void resetPassword(UUID userId, String newPassword) {

    }

    @Override
    public void updateLastLogin(UUID userId) {

    }

    @Override
    public void deleteUser(UUID userId) {

    }

    private User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("User with ID " + id + " not found"));
    }

}
