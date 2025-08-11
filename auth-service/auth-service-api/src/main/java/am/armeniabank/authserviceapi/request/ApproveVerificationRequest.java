package am.armeniabank.authserviceapi.request;

import am.armeniabank.authserviceapi.emuns.VerifierType;
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
public class ApproveVerificationRequest {

    private String verifier;

    private VerifierType verifierType;
}
