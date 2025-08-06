package am.armeniabank.authservice.mapper;

import am.armeniabank.authservice.dto.UserVerificationResponseDto;
import am.armeniabank.authservice.entity.UserVerification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserVerificationMapper {

    UserVerificationResponseDto toUserProfileDto(UserVerification verification);
}
