package com.videototextaudio.core.entity.microservice.translate;

import lombok.Data;

@Data
public class TranslateResponseText {
    private String text;
    private String detectedLanguageCode;
}
