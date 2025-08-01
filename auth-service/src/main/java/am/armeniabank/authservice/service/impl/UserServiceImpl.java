package am.armeniabank.authservice.service.impl;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserUpdateRequest;
import am.armeniabank.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

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
