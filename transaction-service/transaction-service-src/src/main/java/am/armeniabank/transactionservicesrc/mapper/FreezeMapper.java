package am.armeniabank.transactionservicesrc.mapper;

import am.armeniabank.transactionserviceapi.response.FreezeResponse;
import am.armeniabank.transactionservicesrc.entity.Freeze;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FreezeMapper {

    FreezeResponse mapToFreezeResponse(Freeze freeze);

}
