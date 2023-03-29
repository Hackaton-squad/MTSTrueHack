package com.videototextaudio.core.presentatioon.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetAudioRequest {
    @NotNull
    private String url;
    @NotNull
    private long start;
    @NotNull
    private String sentence;
}
