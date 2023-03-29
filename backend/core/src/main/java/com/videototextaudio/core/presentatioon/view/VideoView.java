package com.videototextaudio.core.presentatioon.view;

import com.videototextaudio.core.enums.Processing;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VideoView {
    String url;
    Processing processing;
}
