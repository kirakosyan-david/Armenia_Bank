package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserEmailSearchResponseDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import am.armeniabank.authservice.entity.UserVerification;
import am.armeniabank.authservice.mapper.UserMapper;
import am.armeniabank.authservice.repository.UserRepository;
import am.armeniabank.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserEmailSearchResponseDto searchByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Пользователь с email " + email + " не найден")
        );
        UserProfile profile = user.getProfile();
        UserVerification verification = user.getVerification();
        return userMapper.toSearchDto(user, profile, verification);
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
