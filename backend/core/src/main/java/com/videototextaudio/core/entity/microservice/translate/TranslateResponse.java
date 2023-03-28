package com.videototextaudio.core.entity.microservice.translate;

import lombok.Data;

import java.util.List;

@Data
public class TranslateResponse {
    private List<TranslateResponseText> translations;
}
