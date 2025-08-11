package am.armeniabank.authservicesrc.service;

import am.armeniabank.authserviceapi.response.UserDto;
import am.armeniabank.authserviceapi.request.UserRegistrationRequest;

public interface UserRegisterService {

    UserDto register(UserRegistrationRequest register);

}
