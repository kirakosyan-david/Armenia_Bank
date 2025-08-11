package am.armeniabank.authservicesrc.mapper;

import am.armeniabank.authserviceapi.response.UserVerificationResponse;
import am.armeniabank.authservicesrc.entity.UserVerification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserVerificationMapper {

    UserVerificationResponse toUserProfileDto(UserVerification verification);
}
