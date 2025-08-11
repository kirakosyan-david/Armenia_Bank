package am.armeniabank.authservicesrc.mapper;

import am.armeniabank.authserviceapi.response.UserResponse;
import am.armeniabank.authserviceapi.response.UpdateUserResponse;
import am.armeniabank.authserviceapi.response.UserDto;
import am.armeniabank.authserviceapi.response.UserEmailSearchResponse;
import am.armeniabank.authservicesrc.entity.User;
import am.armeniabank.authservicesrc.entity.UserProfile;
import am.armeniabank.authservicesrc.entity.UserVerification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "userVerification.passportNumber", target = "passportNumber")
    @Mapping(source = "user.emailVerified", target = "emailVerified")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "userProfile.firstName", target = "firstName")
    @Mapping(source = "userProfile.lastName", target = "lastName")
    @Mapping(source = "userProfile.patronymic", target = "patronymic")
    UserDto toDto(User user, UserProfile userProfile, UserVerification userVerification);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.emailVerified", target = "emailVerified")
    @Mapping(source = "user.lastLoginAt", target = "lastLoginAt")
    @Mapping(source = "user.createdAt", target = "createdAt")
    @Mapping(source = "user.updatedAt", target = "updatedAt")
    @Mapping(source = "user.accountLockedUntil", target = "accountLockedUntil")
    @Mapping(source = "userProfile.firstName", target = "firstName")
    @Mapping(source = "userProfile.lastName", target = "lastName")
    @Mapping(source = "userProfile.patronymic", target = "patronymic")
    @Mapping(source = "userVerification.status", target = "status")
    @Mapping(source = "userVerification.passportNumber", target = "passportNumber")
    @Mapping(source = "userVerification.requestedAt", target = "requestedAt")
    @Mapping(source = "userVerification.verifiedAt", target = "verifiedAt")
    @Mapping(source = "userVerification.expiredAt", target = "expiredAt")
    @Mapping(source = "userVerification.bankAccountNumber", target = "bankAccountNumber")
    @Mapping(source = "userVerification.documentUrl", target = "documentUrl")
    @Mapping(source = "userVerification.documentType", target = "documentType")
    @Mapping(source = "userVerification.rejectionReason", target = "rejectionReason")
    @Mapping(source = "userVerification.verificationMethod", target = "verificationMethod")
    @Mapping(source = "userVerification.documentsUploadedAt", target = "documentsUploadedAt")
    @Mapping(source = "userVerification.verifiedBy", target = "verifiedBy")
    @Mapping(source = "userVerification.verifiedByType", target = "verifiedByType")
    @Mapping(source = "userVerification.active", target = "active")
    @Mapping(source = "userVerification.additionalComments", target = "additionalComments")
    UserEmailSearchResponse toSearchDto(User user, UserProfile userProfile, UserVerification userVerification);

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.lastLoginAt", target = "lastLoginAt")
    @Mapping(source = "user.createdAt", target = "createdAt")
    @Mapping(source = "user.updatedAt", target = "updatedAt")
    @Mapping(source = "userProfile.firstName", target = "firstName")
    @Mapping(source = "userProfile.lastName", target = "lastName")
    @Mapping(source = "userProfile.patronymic", target = "patronymic")
    @Mapping(source = "userVerification.status", target = "status")
    @Mapping(source = "userVerification.passportNumber", target = "passportNumber")
    @Mapping(source = "userVerification.requestedAt", target = "requestedAt")
    @Mapping(source = "userVerification.verifiedAt", target = "verifiedAt")
    @Mapping(source = "userVerification.expiredAt", target = "expiredAt")
    @Mapping(source = "userVerification.bankAccountNumber", target = "bankAccountNumber")
    @Mapping(source = "userVerification.documentUrl", target = "documentUrl")
    @Mapping(source = "userVerification.documentType", target = "documentType")
    @Mapping(source = "userVerification.rejectionReason", target = "rejectionReason")
    @Mapping(source = "userVerification.verificationMethod", target = "verificationMethod")
    @Mapping(source = "userVerification.documentsUploadedAt", target = "documentsUploadedAt")
    @Mapping(source = "userVerification.verifiedBy", target = "verifiedBy")
    @Mapping(source = "userVerification.verifiedByType", target = "verifiedByType")
    @Mapping(source = "userVerification.active", target = "active")
    @Mapping(source = "userVerification.additionalComments", target = "additionalComments")
    UserResponse toUserByIdDto(User user, UserProfile userProfile, UserVerification userVerification);

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role", target = "role")
    @Mapping(source = "user.emailVerified", target = "emailVerified")
    @Mapping(source = "userVerification.passportNumber", target = "passportNumber")
    UpdateUserResponse toUserUpdateDto(User user, UserVerification userVerification);
}
