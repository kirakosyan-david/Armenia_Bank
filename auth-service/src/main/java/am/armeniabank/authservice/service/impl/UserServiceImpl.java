package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.emuns.Gender;
import am.armeniabank.authservice.entity.emuns.UserRoles;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserProfileRepository;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDto register(UserRegistrationRequest register) {
        if (userRepository.existsByEmail(register.getEmail())) {
            throw new RuntimeException("Email already used");
        }
        User user = User.builder()
                .email(register.getEmail())
                .password(passwordEncoder.encode(register.getPassword()))
                .passportNumber(register.getPassportNumber())
                .role(UserRoles.USER)
                .emailVerified(false)
                .build();

        UserProfile userProfile = UserProfile.builder()
                .lastName(register.getLastName())
                .firstName(register.getFirstName())
                .gender(Gender.OTHER)
                .user(user)
                .build();

        user.setProfile(userProfile);
        userRepository.save(user);

        return userMapper.toDto(user, userProfile);
    }


    @Override
    public UserDto findByEmail(String email) {
        return null;
    }

    @Override
    public UserDto findById(UUID id) {
        return null;
    }

    @Override
    public UserDto update(UUID id, UserUpdateRequest request) {
        return null;
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
}
