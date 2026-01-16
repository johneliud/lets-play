package io.github.johneliud.letsplay.ratelimit;

import java.util.concurrent.ConcurrentHashMap;

/*
 * Default: 100 requests per hour (0.028 tokens per second)
 *
 * Auth endpoints: 10 requests per minute (0.167 tokens per second)
 * */
public class RateLimitService {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private static final long DEFAULT_CAPACITY = 100;
    private static final double DEFAULT_REFILL_RATE = 100.0 / 3600.0;

    private static final long AUTH_CAPACITY = 10;
    private static final double AUTH_REFILL_RATE = 10.0 / 60.0;

    public boolean isAllowed(String clientId, String endpoint) {
        String key = clientId + ":" + getEndpointGroup(endpoint);
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> createBucket(endpoint));
        return bucket.tryConsume(1);
    }

    public long getRemainingTokens(String clientId, String endpoint) {
        String key = clientId + ":" + getEndpointGroup(endpoint);
        TokenBucket bucket = buckets.get(key);
        return bucket != null ? bucket.getAvailableTokens() : getCapacity(endpoint);
    }

    private TokenBucket createBucket(String endpoint) {
        if (endpoint.startsWith("/auth/")) {
            return new TokenBucket(AUTH_CAPACITY, AUTH_REFILL_RATE);
        }
        return new TokenBucket(DEFAULT_CAPACITY, DEFAULT_REFILL_RATE);
    }

    private String getEndpointGroup(String endpoint) {
        if (endpoint.startsWith("/auth/")) return "auth";
        if (endpoint.startsWith("/users")) return "users";
        if (endpoint.startsWith("/products")) return "products";
        return "default";
    }

    private long getCapacity(String endpoint) {
        return endpoint.startsWith("/auth/") ? AUTH_CAPACITY : DEFAULT_CAPACITY;
    }
}