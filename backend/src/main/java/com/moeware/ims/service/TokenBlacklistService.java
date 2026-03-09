package com.moeware.ims.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing a Redis-based JWT token blacklist.
 * <p>
 * When a user logs out, their token is added to the blacklist with a TTL
 * matching
 * the token's remaining expiry. The JWT filter checks this blacklist on every
 * request.
 * </p>
 *
 * <p>
 * <b>Redis key format:</b> {@code jwt:blacklist:<token>}
 * </p>
 * <p>
 * <b>Dependencies required in pom.xml:</b>
 * </p>
 * 
 * <pre>
 *   &lt;dependency&gt;
 *     &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
 *     &lt;artifactId&gt;spring-boot-starter-data-redis&lt;/artifactId&gt;
 *   &lt;/dependency&gt;
 * </pre>
 * <p>
 * <b>application.yml config required:</b>
 * </p>
 * 
 * <pre>
 *   spring:
 *     data:
 *       redis:
 *         host: ${REDIS_HOST:localhost}
 *         port: ${REDIS_PORT:6379}
 *         password: ${REDIS_PASSWORD:}
 * </pre>
 *
 * @author MoeWare Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    /**
     * Add a JWT token to the blacklist.
     * The entry automatically expires when the token itself would expire,
     * so the blacklist stays clean without manual cleanup.
     *
     * @param token raw JWT token string (without "Bearer " prefix)
     */
    public void blacklistToken(String token) {
        try {
            Date expiration = jwtService.extractExpiration(token);
            long remainingMs = expiration.getTime() - System.currentTimeMillis();

            if (remainingMs > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "blacklisted", remainingMs, TimeUnit.MILLISECONDS);
                log.info("Token blacklisted, expires in {}ms", remainingMs);
            } else {
                log.debug("Token already expired, skipping blacklist entry");
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
            // Don't rethrow — logout should still succeed even if Redis is unavailable
        }
    }

    /**
     * Check whether a token is blacklisted (i.e., has been logged out).
     *
     * @param token raw JWT token string (without "Bearer " prefix)
     * @return {@code true} if the token is blacklisted, {@code false} otherwise
     */
    public boolean isBlacklisted(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Failed to check token blacklist (Redis unavailable?): {}", e.getMessage());
            // Fail open — if Redis is down, don't block all authenticated requests
            return false;
        }
    }
}