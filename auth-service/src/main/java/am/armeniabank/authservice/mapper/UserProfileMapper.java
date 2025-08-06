package am.armeniabank.authservice.mapper;

import am.armeniabank.authservice.dto.UserProfileDto;
import am.armeniabank.authservice.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileDto toUserProfileDto(UserProfile userProfile);
}
