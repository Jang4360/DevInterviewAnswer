package dev.interview.server.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisLockService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean tryLock(String key, Duration timeout) {
        // 밀리초 단위로 변환
        long timeoutMillis = timeout.toMillis();
        // Redis 명령어를 직접 실행하여 NX와 PX 옵션 설정
        Boolean success = redisTemplate.execute((RedisConnection connection) ->
                connection.set(
                        key.getBytes(),
                        "locked".getBytes(),
                        Expiration.milliseconds(timeoutMillis),
                        RedisStringCommands.SetOption.SET_IF_ABSENT)
        );
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}

Boolean locked = redisTemplate.opsForValue()
        .setIfAbsent("generate:" + userId + ":" + writingId, "lock", Duration.ofSeconds(10));
if (!Boolean.TRUE.equals(locked)) {
        throw new DuplicateRequestException("이미 처리 중인 요청입니다.");
}

SET profiling = 1;

-- 파티셔닝 적용 전 조회 성능 측정
SELECT * FROM alarm_data WHERE di_idx = 5 AND ac_idx = 2 ORDER BY reg_date DESC;

SHOW PROFILES;