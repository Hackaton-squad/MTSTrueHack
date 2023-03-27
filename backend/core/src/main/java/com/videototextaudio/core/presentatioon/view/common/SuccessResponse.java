package com.videototextaudio.core.presentatioon.view.common;

import com.videototextaudio.core.enums.ResponseStatus;
import lombok.Data;

@Data
public class SuccessResponse {

    public SuccessResponse() {
        status = ResponseStatus.SUCCESS_RESPONSE.getValue();
    }

    private final String status;
}

