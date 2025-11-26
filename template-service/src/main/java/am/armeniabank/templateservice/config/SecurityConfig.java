package am.armeniabank.templateservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/favicon.ico", "/assets/css/**", "/assets/js/**", "/assets/images/**", "/assets/webfonts/**").permitAll()
                        .requestMatchers("/template/login", "/template/register", "/template/home").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
