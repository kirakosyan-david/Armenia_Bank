package am.armeniabank.authserviceapi.constants;

import java.util.Set;

public class RoleConstants {
    public static final String ROLE_REGEX = "^[A-Z_]+$";

    public static final String ADMIN = "ADMIN";

    public static final String USER = "USER";

    public static final Set<String> UI_FORBIDDEN_ROLES = Set.of(USER, ADMIN);
}
