package com.xdai.rag.chatstorage.ratelimiter;

import java.time.Instant;

public interface RateLimiterService {

    String RATE_LIMIT_PREFIX = "rate-limit";

    boolean isAllowed(String clientId);

    default String getKeyByClientId(String clientId, int windowInSeconds) {
        long window = Instant.now().getEpochSecond() / windowInSeconds;

        return RATE_LIMIT_PREFIX + ":" + clientId + ":" + window;
    }
}
