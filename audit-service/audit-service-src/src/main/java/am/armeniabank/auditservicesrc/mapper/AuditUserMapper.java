package am.armeniabank.auditservicesrc.mapper;

import am.armeniabank.auditserviceapi.response.AuditUserEventResponse;
import am.armeniabank.auditservicesrc.entity.AuditUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditUserMapper {

    AuditUserEventResponse toAuditUser(AuditUser auditUser);

}
