package com.videototextaudio.core.presentatioon.request;

import com.videototextaudio.core.enums.Processing;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SetProcessingRequest {
    @NotNull
    private String url;
    @NotNull
    private Processing processing;
}