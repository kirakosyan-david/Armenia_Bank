package am.armeniabank.authservice.mapper;

import am.armeniabank.authservice.dto.UserDto;
import am.armeniabank.authservice.entity.User;
import am.armeniabank.authservice.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.passportNumber", target = "passportNumber")
    @Mapping(source = "user.emailVerified", target = "emailVerified")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "userProfile.firstName", target = "firstName")
    @Mapping(source = "userProfile.lastName", target = "lastName")
    UserDto toDto(User user, UserProfile userProfile);
}
