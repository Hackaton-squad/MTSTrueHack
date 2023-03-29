package com.videototextaudio.core.presentatioon.view;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AudioView {
    Long start;
    String text;
}
