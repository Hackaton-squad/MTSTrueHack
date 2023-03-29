package com.videototextaudio.core.presentatioon.view;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;
import java.util.List;

@Value
@Builder
public class ListAudioView {
    List<AudioView> audios;
    Long lastTimestamp;
    String videoProcessingStatus;
}
