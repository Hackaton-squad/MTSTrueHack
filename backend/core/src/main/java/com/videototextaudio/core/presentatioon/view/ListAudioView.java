package com.videototextaudio.core.presentatioon.view;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;

@Value
@Builder
public class ListAudioView {
    HashMap<Long, String> audios;
    Long lastTimestamp;
    String videoProcessingStatus;
}
