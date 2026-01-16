package io.github.johneliud.letsplay.ratelimit;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket {
    private final long capacity;
    private final double refillRate;
    private final AtomicLong tokens;
    private volatile long lastRefillTime;

    public TokenBucket(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = Instant.now().getEpochSecond();
    }

    public synchronized boolean tryConsume(long tokensToConsume) {
        refill();
        long currentTokens = tokens.get();
        if (currentTokens >= tokensToConsume) {
            tokens.addAndGet(-tokensToConsume);
            return true;
        }
        return false;
    }

    private void refill() {
        long now = Instant.now().getEpochSecond();
        double tokensToAdd = (now - lastRefillTime) * refillRate;
        if (tokensToAdd > 0) {
            tokens.set(Math.min(capacity, tokens.get() + (long) tokensToAdd));
            lastRefillTime = now;
        }
    }

    public long getAvailableTokens() {
        refill();
        return tokens.get();
    }
}