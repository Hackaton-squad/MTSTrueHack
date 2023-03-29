package com.videototextaudio.core.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.videototextaudio.core.enums.RedisHashKey.URL;

@Repository
@RequiredArgsConstructor
public class UrlRepositoryImpl {
    private final RedisTemplate<String, String> template;

    public void addUrl(String url) {
        template.opsForSet().add(URL.name(), url);
    }

    public Set<String> getAllUrls() {
        return Optional.ofNullable(template.opsForSet().members(URL.name())).orElse(new HashSet<>());
    }

}
