package com.videototextaudio.core.controller;

import com.videototextaudio.core.entity.Audio;
import com.videototextaudio.core.presentatioon.request.GetAudioRequest;
import com.videototextaudio.core.presentatioon.request.SetAudioRequest;
import com.videototextaudio.core.presentatioon.request.SetProcessingRequest;
import com.videototextaudio.core.presentatioon.view.AudioView;
import com.videototextaudio.core.presentatioon.view.ListAudioView;
import com.videototextaudio.core.presentatioon.view.common.SuccessResponse;
import com.videototextaudio.core.service.AudioServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class VideoConvertorController {

    private final AudioServiceImpl audioService;

    @Operation(summary = "Get text from video")
    @GetMapping(value = "/download")
    public ListAudioView getAudios(@RequestParam String url,
                                   @RequestParam String srturl,
                                   @RequestParam long start,
                                   @RequestParam long end) {
        var request = GetAudioRequest.builder().url(url).srturl(srturl).start(start).end(end).build();
        log.info("Request for getting audio: {}", request);
        return ListAudioView.builder()
                .audios(audioService.getAudios(request).stream().collect(Collectors.toMap(Audio::getStart, Audio::getText, (x, y) -> y, LinkedHashMap::new)))
                .videoProcessingStatus(audioService.getAudioStatus(request.getUrl()).name())
                .build();
    }

    @Operation(summary = "Set text for timestamp for video")
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponse setAudio(@Valid @RequestBody SetAudioRequest request) {
        log.info("Request for setting audio: {}", request);
        audioService.setAudio(request);
        return new SuccessResponse();
    }

    @Operation(summary = "Set status for timestamp")
    @PostMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SuccessResponse setStatus(@Valid @RequestBody SetProcessingRequest request) {
        log.info("Request for setting audio status: {}", request);
        audioService.setAudioStatus(request);
        return new SuccessResponse();
    }
}