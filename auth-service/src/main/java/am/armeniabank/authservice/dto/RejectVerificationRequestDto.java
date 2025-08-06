package am.armeniabank.authservice.dto;

import am.armeniabank.authservice.entity.emuns.RejectionReason;
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
public class RejectVerificationRequestDto {

    private RejectionReason rejectionReason;

    private String comment;
}
