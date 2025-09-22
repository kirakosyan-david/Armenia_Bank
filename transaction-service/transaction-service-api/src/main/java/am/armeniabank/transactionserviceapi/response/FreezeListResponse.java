package am.armeniabank.transactionserviceapi.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeListResponse {

    private List<FreezeResponse> freezes;
    private String message;
}
