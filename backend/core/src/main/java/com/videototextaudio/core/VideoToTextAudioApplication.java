package com.videototextaudio.core;

import com.videototextaudio.core.repository.AudioRepositoryImpl;
import com.videototextaudio.core.service.parser.TimestampParserImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
@OpenAPIDefinition
public class VideoToTextAudioApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoToTextAudioApplication.class, args);
    }
}

@Component
@RequiredArgsConstructor
@Slf4j
 class CommandLineAppStartupRunner implements CommandLineRunner {

    private final AudioRepositoryImpl audioRepository;

    @Override
    public void run(String...args) throws Exception {
//        log.info("Remove");
//
//        long l = System.currentTimeMillis();
//        audioRepository.removeAll("test");
//        log.info(String.valueOf(System.currentTimeMillis() - l));
//
//        log.info("Add");
//
//        l = System.currentTimeMillis();
//        for(int i = 0; i < 3600; i++) audioRepository.save("test", i, "sentence " + i);
//        log.info(String.valueOf(System.currentTimeMillis() - l));
//
//        log.info("Read");
//
//        l = System.currentTimeMillis();
//        var testString = audioRepository.getAllTimestampsString("test");
//        log.info(testString.toString());
//        log.info(String.valueOf(System.currentTimeMillis() - l));
    }
}