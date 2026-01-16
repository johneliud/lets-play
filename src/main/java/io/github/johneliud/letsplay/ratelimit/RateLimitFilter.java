package io.github.johneliud.letsplay.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientId = getClientId(request);
        String endpoint = request.getRequestURI();

        if (!rateLimitService.isAllowed(clientId, endpoint)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                        "timestamp": "%s",
                        "status": 429,
                        "error": "Too Many Requests",
                        "message": "Rate limit exceeded. Try again later.",
                        "path": "%s"
                    }
                    """.formatted(java.time.LocalDateTime.now(), endpoint));
            return;
        }

        // Rate limit headers
        long remaining = rateLimitService.getRemainingTokens(clientId, endpoint);
        response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
        response.setHeader("X-RateLimit-Limit", getLimit(endpoint));

        filterChain.doFilter(request, response);
    }

    private String getClientId(HttpServletRequest request) {
        // Use IP address as a client identifier
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String getLimit(String endpoint) {
        return endpoint.startsWith("/auth/") ? "10" : "100";
    }
}