package am.armeniabank.authservicesrc.kafka.model;

import am.armeniabank.authservicesrc.kafka.model.enumeration.UserEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEvent {

    private UUID id;

    private String firstName;

    private String lastName;

    private String patronymic;

    private String email;

    private boolean emailVerified;

    private String role;

    private UserEventType type;

    private LocalDateTime createdAt;
}
