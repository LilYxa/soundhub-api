package com.soundhub.api.security;

import com.soundhub.api.Constants;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BlacklistingService {

    @CachePut(value = Constants.BLACKLIST_CACHE_NAME)
    public String blackListJwt(String jwt) {
        return jwt;
    }

    @Cacheable(value = Constants.BLACKLIST_CACHE_NAME, unless = "#result == null")
    public String getJwtBlacklist(String jwt) {
        return null;
    }
}
