package com.videototextaudio.core.repository;

import com.videototextaudio.core.service.parser.TimestampParserImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.videototextaudio.core.enums.RedisHashKey.SENTENCE;
import static com.videototextaudio.core.enums.RedisHashKey.TIMESTAMP;
import static com.videototextaudio.core.util.GetHashKey.getKey;

@Repository
@RequiredArgsConstructor
public class AudioRepositoryImpl {
    private final RedisTemplate<String, String> template;
    private final TimestampParserImpl timestampParser;

    public List<Long> getAllTimestamps(String url) {
        return Optional.ofNullable(template.opsForList().range(getKey(TIMESTAMP, url), 0, -1))
                .orElse(new ArrayList<>())
                .stream().map(timestampParser::stringToTimestamp).collect(Collectors.toList());
    }

    public Long getLastTimestamp(String url) {
        return Optional.ofNullable(template.opsForList().range(getKey(TIMESTAMP, url), -1, -1))
                .orElse(new ArrayList<>())
                .stream().map(timestampParser::stringToTimestamp).findFirst().orElse(0L);
    }

    public List<String> getSentences(String url, int left, int right) {
        return template.opsForList().range(getKey(SENTENCE, url), left, right);
    }

    public void save(String url, long timestamp, String sentence) {
        template.opsForList().rightPush(getKey(TIMESTAMP, url), String.valueOf(timestamp));
        template.opsForList().rightPush(getKey(SENTENCE, url), sentence);
    }

    public void removeAll(String url) {
        template.delete(getKey(TIMESTAMP, url));
        template.delete(getKey(SENTENCE, url));
    }
}
