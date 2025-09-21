package am.armeniabank.transactionservicesrc.util;

import am.armeniabank.transactionservicesrc.exception.custam.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class SecurityUtils {

    public static String getCurrentToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }

        log.error("JWT token not found in SecurityContext");
        throw new IllegalStateException("JWT token not found in SecurityContext");
    }

    public static UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            log.error("JWT authentication not found in SecurityContext");
            throw new UserNotFoundException("JWT authentication not found in SecurityContext");
        }

        String sub = jwtAuth.getToken().getSubject();
        log.debug("Extracted sub from JWT: {}", sub);

        if (sub == null) {
            throw new UserNotFoundException("User Id (sub) not found in JWT token");
        }

        try {
            return UUID.fromString(sub);
        } catch (IllegalArgumentException e) {
            log.error("Invalid UUID format in JWT sub: {}", sub, e);
            throw new UserNotFoundException("Invalid UUID format in JWT sub: " + sub, e);
        }
    }
}
