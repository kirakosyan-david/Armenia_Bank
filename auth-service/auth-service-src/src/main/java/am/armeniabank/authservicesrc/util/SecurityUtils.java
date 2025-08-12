package am.armeniabank.authservicesrc.util;

import am.armeniabank.authservicesrc.security.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new RuntimeException("User not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof CurrentUser) {
            CurrentUser currentUser = (CurrentUser) principal;
            return currentUser.getUser().getId();
        }
        throw new RuntimeException("Unsupported principal type: " + principal.getClass());
    }

}
