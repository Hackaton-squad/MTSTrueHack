package com.videototextaudio.core.controller;

import com.videototextaudio.core.entity.Audio;
import com.videototextaudio.core.enums.TranslateLang;
import com.videototextaudio.core.presentatioon.request.*;
import com.videototextaudio.core.presentatioon.view.ListAudioView;
import com.videototextaudio.core.presentatioon.view.VideoView;
import com.videototextaudio.core.presentatioon.view.common.SuccessResponse;
import com.videototextaudio.core.service.AudioServiceImpl;
import com.videototextaudio.core.service.microservice.TranslationMicroserviceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VideoConvertorController {

    private final AudioServiceImpl audioService;
    //private final TranslationMicroserviceImpl translationService;

    @Operation(summary = "Get all videos info")
    @GetMapping()
    public List<VideoView> getVideo() {
        log.info("Request for getting all video info");
        return audioService.getVideoUrls().stream()
                .map(url -> VideoView.builder()
                        .url(url)
                        .processing(audioService.getAudioStatus(url))
                        .build())
                .collect(Collectors.toList());
    }

    @Operation(summary = "Add video info")
    @PostMapping()
    public SuccessResponse setVideo(@Valid @RequestBody SetVideoRequest request) {
        log.info("Request for setting video: {}", request);
        audioService.setVideo(request);
        return new SuccessResponse();
    }

    @Operation(summary = "Get text from video")
    @GetMapping(value = "/download")
    public ListAudioView getAudios(@RequestParam String url,
                                   @RequestParam long start,
                                   @RequestParam long end,
                                   @RequestParam(required = false, defaultValue = "DEFAULT") TranslateLang lang) {
        var request = GetAudioRequest.builder().url(url).start(start).end(end).lang(lang).build();
        log.info("Request for getting audio: {}", request);
        return ListAudioView.builder()
                .audios(audioService.getAudios(request).stream().collect(Collectors.toMap(Audio::getStart, Audio::getText, (x, y) -> y, LinkedHashMap::new)))
                .videoProcessingStatus(audioService.getAudioStatus(request.getUrl()).name())
                .lastTimestamp(audioService.getLastTimeStamp(request.getUrl()))
                .build();
    }

    @Operation(summary = "Set text for timestamp for video")
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponse setAudio(@Valid @RequestBody SetAudioRequest request) {
        log.info("Request for setting audio: {}", request);
        audioService.setAudio(request);
        return new SuccessResponse();
    }

    @Operation(summary = "Set status for video")
    @PostMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponse setStatus(@Valid @RequestBody SetProcessingRequest request) {
        log.info("Request for setting audio status: {}", request);
        audioService.setAudioStatus(request);
        return new SuccessResponse();
    }

    @Operation(summary = "Request a processing of a video")
    @PostMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponse process(@Valid @RequestBody StartProcessingRequest request) {
        log.info("Request for process the url: {}", request);
        audioService.startProcessing(request);
        return new SuccessResponse();
    }

//    @Operation(summary = "Translation")
//    @GetMapping(value = "/translation")
//    public List<String> translate(@RequestParam String[] text) {
//        log.info("Translate this: {}", (Object) text);
//        return translationService.translate(List.of(text), TranslateLang.RU);
//    }
}
