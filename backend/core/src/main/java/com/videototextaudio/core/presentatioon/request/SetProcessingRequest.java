package com.videototextaudio.core.presentatioon.request;

import com.videototextaudio.core.enums.Processing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetProcessingRequest {
    @NotNull
    private String url;
    @NotNull
    private Processing processing;
}