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
public class StartProcessingRequest {
    @NotNull
    private String url;
    private String srturl;
    private Boolean hard = false;
}
