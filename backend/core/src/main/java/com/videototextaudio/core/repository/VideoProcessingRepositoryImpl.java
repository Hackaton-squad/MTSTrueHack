package com.videototextaudio.core.repository;

import com.videototextaudio.core.enums.Processing;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static com.videototextaudio.core.enums.RedisHashKey.PROCESSING;
import static com.videototextaudio.core.util.GetHashKey.getKey;

@Repository
@RequiredArgsConstructor
public class VideoProcessingRepositoryImpl {
    private final RedisTemplate<String, String> template;

    public Processing get(String url) {
        var state = template.opsForValue().get(getKey(PROCESSING, url));
        if (state == null) return Processing.NOT_PROCESSED;
        return Processing.valueOf(state);
    }

    public void save(String url, Processing state) {
        template.opsForValue().set(getKey(PROCESSING, url), state.toString());
    }

    //------------------------------------------------------------------------------------------------------------------

    public void remove(String url){
        template.delete(getKey(PROCESSING, url));
    }
}
