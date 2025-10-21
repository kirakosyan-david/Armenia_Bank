package am.armeniabank.notificationservicesrc.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Notification Service API", version = "v1"),
        security = @SecurityRequirement(name = "keycloak"))
@SecurityScheme(name = "keycloak", type = SecuritySchemeType.OAUTH2, in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "http://localhost:8080/realms/ArmeniaBank/protocol/openid-connect/auth",
                tokenUrl = "http://localhost:8080/realms/ArmeniaBank/protocol/openid-connect/token",
                scopes = {
                        @OAuthScope(name = "openid", description = "OpenID Connect"),
                        @OAuthScope(name = "notification", description = "Notification profile"),})))
public class OpenAPIConfig {
}
