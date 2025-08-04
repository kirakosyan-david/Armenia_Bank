package am.armeniabank.authservice.service;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.dto.UserRegistrationRequest;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public interface UserRegisterService {

    UserDto register(UserRegistrationRequest register);

}
