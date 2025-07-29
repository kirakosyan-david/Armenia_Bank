package am.armeniabank.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("backend-gateway-client")
                .clientId("backend-gateway-client")
                .clientSecret("hmSaYWmiA9pTIVpj5nBSfrdDONlmToBO")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("http://backend-keycloak-auth:8080/auth/realms/ArmeniaBank/protocol/openid-connect/auth")
                .tokenUri("http://backend-keycloak-auth:8080/auth/realms/ArmeniaBank/protocol/openid-connect/token")
                .userInfoUri("http://backend-keycloak-auth:8080/auth/realms/ArmeniaBank/protocol/openid-connect/userinfo")
                .userNameAttributeName("preferred_username")
                .jwkSetUri("http://backend-keycloak-auth:8080/auth/realms/ArmeniaBank/protocol/openid-connect/certs")
                .clientName("keycloak")
                .build();

        return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
    }
}