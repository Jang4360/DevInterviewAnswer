package dev.interview.server.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key, Duration timeout) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", timeout);
        return Boolean.TRUE.equals(success);
    }
    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
