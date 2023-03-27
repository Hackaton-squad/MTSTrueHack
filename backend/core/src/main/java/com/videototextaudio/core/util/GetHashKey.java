package com.videototextaudio.core.util;

import com.videototextaudio.core.enums.RedisHashKey;

public class GetHashKey {
    public static String getKey(RedisHashKey hashKey, String value){
        return String.format("%s:%s", hashKey, value);
    }
}