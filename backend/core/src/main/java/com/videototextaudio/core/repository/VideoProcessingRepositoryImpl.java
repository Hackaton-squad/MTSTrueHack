package com.videototextaudio.core.repository;

import com.videototextaudio.core.enums.Processing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.videototextaudio.core.enums.RedisHashKey.*;
import static com.videototextaudio.core.util.GetHashKey.getKey;

@Repository
@RequiredArgsConstructor
public class VideoProcessingRepositoryImpl {
    private final RedisTemplate<String, String> template;

    @Value("${ttl.redis.processing-timer}")
    private long ttl;

    public Processing get(String url) {
        var state = template.opsForValue().get(getKey(PROCESSING, url));
        if (state == null) return Processing.NOT_PROCESSED;
        return Processing.valueOf(state);
    }

    public void save(String url, Processing state) {
        template.opsForValue().set(getKey(PROCESSING, url), state.toString());
    }

    public void remove(String url){
        template.delete(getKey(PROCESSING, url));
    }

    public void updateTimer(String url){
        template.opsForValue().set(getKey(PROCESSING_TIMER, url), "_");
        template.expire(getKey(PROCESSING_TIMER, url), ttl, TimeUnit.SECONDS);
    }

    public boolean isExpired(String url){
        return template.opsForValue().get(getKey(PROCESSING_TIMER, url)) == null;
    }
}
