package com.xdai.rag.chatstorage.ratelimiter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Profile("local")
public class InMemoryRateLimiterService implements RateLimiterService {

    private final Map<String, Long> map = new ConcurrentHashMap<>();

    @Value("${app.rate.limit.requests:5}")
    private int maxRequestsAllowed;

    @Value("${app.rate.limit.windowSeconds:60}")
    private int windowInSeconds;

    @Override
    public boolean isAllowed(String clientId) {
        try {
            String key = getKeyByClientId(clientId, windowInSeconds);

            Long currentCount = map.merge(key, 1L, Long::sum);
            if (currentCount == 1) {
                scheduleKeyExpiry(key);
            }

            if (currentCount > maxRequestsAllowed) {
                log.warn("Rate limit exceeded for client [{}] ({} req/{}s)", clientId, currentCount, windowInSeconds);
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("Rate limiting failed for client [{}]: {}", clientId, e.getMessage(), e);
            return true;
        }
    }

    private void scheduleKeyExpiry(String key) {
        new Thread(() -> {
            try {
                Thread.sleep(windowInSeconds * 1000L);
                map.remove(key);
                log.debug("Expired rate limit window for key [{}]", key);
            } catch (InterruptedException ignored) { }
        }).start();
    }
}