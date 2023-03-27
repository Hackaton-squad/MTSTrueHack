package com.videototextaudio.core.presentatioon.request;

import com.videototextaudio.core.enums.Processing;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class SetProcessingRequest {
    @NotNull
    private String url;
    @NotNull
    private Processing processing;
}