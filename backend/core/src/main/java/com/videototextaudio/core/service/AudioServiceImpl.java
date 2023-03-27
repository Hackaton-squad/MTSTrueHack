package com.videototextaudio.core.service;

import com.videototextaudio.core.entity.Audio;
import com.videototextaudio.core.entity.LeftRightPointers;
import com.videototextaudio.core.enums.Processing;
import com.videototextaudio.core.presentatioon.request.GetAudioRequest;
import com.videototextaudio.core.presentatioon.request.SetAudioRequest;
import com.videototextaudio.core.presentatioon.request.SetProcessingRequest;
import com.videototextaudio.core.presentatioon.request.SetVideoRequest;
import com.videototextaudio.core.presentatioon.view.VideoView;
import com.videototextaudio.core.repository.AudioRepositoryImpl;
import com.videototextaudio.core.repository.UrlRepositoryImpl;
import com.videototextaudio.core.repository.VideoProcessingRepositoryImpl;
import com.videototextaudio.core.service.microservice.VideoMicroserviceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AudioServiceImpl {

    private final VideoMicroserviceImpl videoManager;
    private final AudioRepositoryImpl audioRepository;
    private final VideoProcessingRepositoryImpl videoProcessingRepository;
    private final UrlRepositoryImpl urlRepository;

    public Set<String> getVideoUrls() {
        return urlRepository.getAllUrls();
    }

    public void setVideo(SetVideoRequest request) {
        urlRepository.addUrl(request.getUrl());
    }

    public List<Audio> getAudios(GetAudioRequest request) {
        if (Processing.NOT_PROCESSED.equals(videoProcessingRepository.get(request.getUrl())))
            generateAudio(request);

        return retrieveAudio(request);
    }

    public Processing getAudioStatus(String url) {
        return videoProcessingRepository.get(url);
    }

    public void setAudio(SetAudioRequest request) {
        audioRepository.save(request.getUrl(), request.getStart(), request.getSentence());
        videoProcessingRepository.updateTimer(request.getUrl());
    }

    public void setAudioStatus(SetProcessingRequest request) {
        videoProcessingRepository.save(request.getUrl(), request.getProcessing());
    }

    private void generateAudio(GetAudioRequest request){
        var connected = videoManager.sendVideoToConversation(request.getUrl(), request.getSrturl());
        if (connected) {
            audioRepository.removeAll(request.getUrl());
            videoProcessingRepository.remove(request.getUrl());

            videoProcessingRepository.save(request.getUrl(), Processing.PROCESSING);
            videoProcessingRepository.updateTimer(request.getUrl());
            urlRepository.addUrl(request.getUrl());
        }
    }

    private List<Audio> retrieveAudio(GetAudioRequest request) {
        var allTimestamps = audioRepository.getAllTimestamps(request.getUrl());

        var lr = getLeftRightPointers(allTimestamps, request.getStart(), request.getEnd());

        var selectedSentences = audioRepository.getSentences(request.getUrl(), lr.getLeft(), lr.getRight());

        var audios = new ArrayList<Audio>();
        int i = lr.getLeft();
        for (var sentence : selectedSentences) {
            audios.add(Audio.builder()
                    .start(allTimestamps.get(i))
                    .text(sentence)
                    .build());
            i++;
        }
        return audios;
    }

    private LeftRightPointers getLeftRightPointers(List<Long> timestamps, long start, long end) {
        int n = timestamps.size();
        int left = 0;
        int right = 0;

        int low = 0, high = n - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (timestamps.get(mid) <= start) {
                left = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        low = 0;
        high = n - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (timestamps.get(mid) >= end) {
                right = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return new LeftRightPointers(left, right - 1);
    }
}
