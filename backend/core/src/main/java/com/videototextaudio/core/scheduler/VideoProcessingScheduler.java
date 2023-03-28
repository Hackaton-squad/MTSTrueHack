package com.videototextaudio.core.scheduler;

import com.videototextaudio.core.enums.Processing;
import com.videototextaudio.core.repository.AudioRepositoryImpl;
import com.videototextaudio.core.repository.UrlRepositoryImpl;
import com.videototextaudio.core.repository.VideoProcessingRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class VideoProcessingScheduler {

    private final VideoProcessingRepositoryImpl videoProcessingRepository;
    private final AudioRepositoryImpl audioRepository;
    private final UrlRepositoryImpl urlRepository;

    @Scheduled(fixedRate = 3_600_000)
//    @Scheduled(fixedRate = 20_000)
    public void refreshStories() {
        log.info("Clear dead processes");
        for (var url : urlRepository.getAllUrls()){
            if(videoProcessingRepository.isExpired(url) && Processing.PROCESSING.equals(videoProcessingRepository.get(url)))
                clearUrlInfo(url);
        }
    }

    private void clearUrlInfo(String url) {
        audioRepository.removeAll(url);
        videoProcessingRepository.remove(url);
    }
}
