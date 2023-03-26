package com.videototextaudio.core.service.manager;

import org.springframework.stereotype.Component;

@Component
public class VideoManagerImpl {

    public void sendVideoToConversation(String url){
        //TODO
        new Thread(this::sendVideo);
    }

    private void sendVideo(){
        //TODO connect to service
    }
}
