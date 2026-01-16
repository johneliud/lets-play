package io.github.johneliud.letsplay.config;

import io.github.johneliud.letsplay.ratelimit.RateLimitService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimitService rateLimitService() {
        return new RateLimitService();
    }
}