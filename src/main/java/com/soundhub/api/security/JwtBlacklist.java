package com.soundhub.api.security;

import com.soundhub.api.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtBlacklist {
    private final RedisTemplate<String, String> redisTemplate;
    public void addToBlacklist(String token) {
        redisTemplate.opsForValue().set(Constants.BLACKLIST_KEY_PREFIX + token, token);
    }

    public Boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(Constants.BLACKLIST_KEY_PREFIX + token);
    }
}
