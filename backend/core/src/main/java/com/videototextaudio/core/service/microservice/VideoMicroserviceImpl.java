package com.videototextaudio.core.service.microservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Component
@Slf4j
public class VideoMicroserviceImpl {

    @Value("${microservice.ml.host}")
    private String mlHost;
    @Value("${microservice.ml.port}")
    private String mlPort;
    @Value("${microservice.ml.rout.convert}")
    private String mlConvertRouting;

    private final RestTemplate restTemplate;

    public VideoMicroserviceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public boolean sendVideoToConversation(String url, String srturl) {
        var requestUrl = "http://" + mlHost + ":" + mlPort + mlConvertRouting;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var body = new HashMap<String, Object>();
        body.put("url", url);
        body.put("subtitles", srturl);

        try {
            var response = restTemplate.postForEntity(requestUrl, new HttpEntity<>(body, headers), String.class);
            log.info("Response from ml: {}", response.getStatusCode().name());
            return HttpStatus.OK.equals(response.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error("Ml microservice error :" + e.getMessage());
            return false;
        }
    }
}
