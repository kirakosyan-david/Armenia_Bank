package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface UserRegisterService {
    @Transactional(isolation = Isolation.READ_COMMITTED)
    UserDto register(UserRegistrationRequest register);
}
