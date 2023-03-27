package com.videototextaudio.core.entity;

import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class Audio {
    private long start;
    private String text;
}
