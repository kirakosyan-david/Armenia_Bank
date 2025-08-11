package am.armeniabank.authserviceapi.request;

import am.armeniabank.authserviceapi.emuns.RejectionReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectVerificationRequest {

    private RejectionReason rejectionReason;

    private String comment;
}
