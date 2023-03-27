package com.videototextaudio.core.presentatioon.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetVideoRequest {
    private String url;
}
