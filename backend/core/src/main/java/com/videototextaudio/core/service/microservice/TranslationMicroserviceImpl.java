package com.videototextaudio.core.service.microservice;

import com.videototextaudio.core.entity.microservice.translate.TranslateResponse;
import com.videototextaudio.core.entity.microservice.translate.TranslateResponseText;
import com.videototextaudio.core.enums.TranslateLang;
import com.videototextaudio.core.exception.FatalApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TranslationMicroserviceImpl {
    @Value("${microservice.translate.url}")
    private String translateUrl;
    @Value("${yandex.translate.api-ket}")
    private String translateApiKey;


    private final RestTemplate restTemplate;

    public TranslationMicroserviceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<String> translate(List<String> texts, TranslateLang lang) {
        var requestUrl = translateUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Api-Key " + translateApiKey);

        var body = new HashMap<String, Object>();
        body.put("targetLanguageCode", lang.toString().toLowerCase());
        body.put("texts", texts);

        var request = new HttpEntity<>(body, headers);

        try {
            var response = restTemplate.postForEntity(requestUrl, request, TranslateResponse.class);

            if (response.getBody() == null) throw new FatalApplicationException("Yandex translation not fulfilled");

            return response.getBody().getTranslations().stream().map(TranslateResponseText::getText).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Yandex.Translate microservice error :" + e.getMessage());
            return null;
        }
    }
}
