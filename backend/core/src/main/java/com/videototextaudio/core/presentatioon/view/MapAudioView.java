package com.videototextaudio.core.presentatioon.view;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;

@Value
@Builder
public class MapAudioView {
    HashMap<Long, String> audios;
    Long lastTimestamp;
    String videoProcessingStatus;
}
