package com.xdai.rag.chatstorage.ratelimiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Profile({"dev", "prod"})
@Slf4j
public class RedisRateLimiterService implements RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.rate.limit.requests:5}")
    private int maxRequestsAllowed;

    @Value("${app.rate.limit.windowSeconds:60}")
    private int windowInSeconds;

    @Override
    public boolean isAllowed(String clientId) {
        try {
            String key = getKeyByClientId(clientId, windowInSeconds);

            Long currentCount = redisTemplate.opsForValue().increment(key);
            if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(key, Duration.ofSeconds(windowInSeconds));
            }

            if (currentCount != null && currentCount > maxRequestsAllowed) {
                log.warn("Rate limit exceeded for client [{}] ({} req/{}s)", clientId, currentCount, windowInSeconds);
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("Rate limiting failed for client [{}]: {}", clientId, e.getMessage(), e);
            return true;
        }
    }
}