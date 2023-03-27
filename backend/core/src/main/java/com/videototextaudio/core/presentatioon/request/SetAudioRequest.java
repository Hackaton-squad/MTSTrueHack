package com.videototextaudio.core.presentatioon.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class SetAudioRequest {
    @NotNull
    private String url;
    @NotNull
    private long start;
    @NotNull
    private String sentence;
}