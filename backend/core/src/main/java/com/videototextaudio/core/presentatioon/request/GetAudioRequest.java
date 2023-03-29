package com.videototextaudio.core.presentatioon.request;

import com.videototextaudio.core.enums.TranslateLang;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAudioRequest {
    @NotNull
    private String url;
    @NotNull
    private long start;
    @NotNull
    private long end;
    @NotNull
    private TranslateLang lang;
}
