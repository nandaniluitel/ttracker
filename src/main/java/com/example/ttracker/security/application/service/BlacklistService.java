package com.example.ttracker.security.application.service;

import com.example.ttracker.security.application.port.out.TokenPort;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class BlacklistService {
    private final RedisTemplate<String, String> redis;
    private final TokenPort tokenPort;

    public BlacklistService(RedisTemplate<String, String> redis, TokenPort tokenPort) {
        this.redis = redis;
        this.tokenPort = tokenPort;
    }

    public void blacklist(String token) {
        long expiry = tokenPort.getRemainingMillis(token);
        redis.opsForValue().set("blacklist:" + token, "true");
       redis.expire("blacklist:" + token, Duration.ofMillis(expiry));
    }
    public boolean isBlacklisted(String token){
        return Boolean.TRUE.equals(redis.hasKey("blacklist:"+token));
    }

    }



