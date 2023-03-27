package com.videototextaudio.core.service.parser;

import org.springframework.stereotype.Component;

@Component
public class TimestampParserImpl {

    public long stringToTimestamp(String timestamp){
        return Long.parseLong(timestamp);
    }
}
