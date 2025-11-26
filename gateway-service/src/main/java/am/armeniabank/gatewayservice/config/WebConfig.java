package am.armeniabank.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

@Configuration
public class WebConfig {

    @Bean
    public WebFilter forwardedHeaderFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            exchange.getRequest().mutate().build();
            return chain.filter(exchange);
        };
    }
}
