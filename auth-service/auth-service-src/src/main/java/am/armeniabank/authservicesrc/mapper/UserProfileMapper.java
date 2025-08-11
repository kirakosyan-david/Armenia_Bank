package am.armeniabank.authservicesrc.mapper;

import am.armeniabank.authserviceapi.response.UserProfileResponse;
import am.armeniabank.authservicesrc.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse toUserProfileDto(UserProfile userProfile);
}
