package com.videototextaudio.core.presentatioon.view;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class ListAudioView {
    List<AudioView> audios;
    String videoProcessingStatus;
}
