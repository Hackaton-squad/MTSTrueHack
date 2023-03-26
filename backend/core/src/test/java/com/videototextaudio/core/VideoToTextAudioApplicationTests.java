package com.videototextaudio.core;

import com.videototextaudio.core.controller.VideoConvertorController;
import com.videototextaudio.core.presentatioon.request.SetAudioRequest;
import com.videototextaudio.core.repository.AudioRepositoryImpl;
import com.videototextaudio.core.repository.VideoProcessingRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VideoToTextAudioApplicationTests {

    private final VideoConvertorController controller;
    private final AudioRepositoryImpl audioRepository;
    private final VideoProcessingRepositoryImpl videoProcessingRepository;

    public VideoToTextAudioApplicationTests(@Autowired VideoConvertorController controller,
                                            @Autowired AudioRepositoryImpl audioRepository,
                                            @Autowired VideoProcessingRepositoryImpl videoProcessingRepository) {
        this.controller = controller;
        this.audioRepository = audioRepository;
        this.videoProcessingRepository = videoProcessingRepository;
    }

    @Test
    void getExistingAudiosOne() {
        long l = System.currentTimeMillis();
        System.out.println("getExistingAudiosOne()");

        final String URL = "http://getExistingAudiosOne";
        generate(URL);
        var audios = controller.getAudios(URL, URL, 3, 4);
        Assertions.assertEquals(0, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(1, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    void getExistingAudiosMany() {
        long l = System.currentTimeMillis();
        System.out.println("getExistingAudiosMany()");

        final String URL = "http://getExistingAudiosMany";
        generate(URL);
        var audios = controller.getAudios(URL, URL, 6, 16);
        Assertions.assertEquals(5, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(3, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    void getRightEquals() {
        long l = System.currentTimeMillis();
        System.out.println("getRightEquals()");

        final String URL = "http://getRightEquals";
        generate(URL);
        var audios = controller.getAudios(URL, URL, 6, 15);
        Assertions.assertEquals(5, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(2, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    void getLeftEquals() {
        long l = System.currentTimeMillis();
        System.out.println("getLeftEquals()");

        final String URL = "http://getLeftEquals";
        generate(URL);
        var audios = controller.getAudios(URL, URL, 5, 16);
        Assertions.assertEquals(5, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(3, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    void getMoreThanRightBorder() {
        long l = System.currentTimeMillis();
        System.out.println("getMoreThanRightBorder()");

        final String URL = "http://getMoreThanRightBorder";
        generate(URL);
        var audios = controller.getAudios(URL, URL, 3, 120);
        Assertions.assertEquals(0, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(20, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    void getLessThanLeftBorder() {
        long l = System.currentTimeMillis();
        System.out.println("getLessThanLeftBorder()");

        final String URL = "http://getLessThanLeftBorder";
        generate(URL);
        var audios = controller.getAudios(URL, URL, -1, 1);
        Assertions.assertEquals(0, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(1, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    public void getAll() {
        long l = System.currentTimeMillis();
        System.out.println("getAll()");

        final String URL = "http://getAll";
        generate(URL);

        var audios = controller.getAudios(URL, URL, -1, -1);
        Assertions.assertEquals(0, audios.getAudios().keySet().stream().findFirst().get());
        Assertions.assertEquals(20, audios.getAudios().size());

        System.out.println(System.currentTimeMillis() - l);
        System.out.println("------------------------------------------");
    }

    @Test
    void benchmarks() {
        final String url = "http://test";
        long l = 0;

        System.out.println("Remove");

        l = System.currentTimeMillis();
        audioRepository.removeAll(url);
        System.out.println(System.currentTimeMillis() - l);

        System.out.println("Add");

        l = System.currentTimeMillis();
        for (int i = 0; i < 3600; i++) audioRepository.save(url, i, "sentence " + i);
        System.out.println(System.currentTimeMillis() - l);

        System.out.println("Read");

        l = System.currentTimeMillis();
        var testString = audioRepository.getAllTimestamps(url);
        System.out.println(testString.toString());
        System.out.println(System.currentTimeMillis() - l);

        System.out.println("------------------------------------------");
    }

    @Test
    public void fill() {
        final String url = "http://generate";
        generate(url);
    }

    private void generate(String url) {
        audioRepository.removeAll(url);
        videoProcessingRepository.remove(url);
        for (int i = 0; i < 100; i += 5)
            controller.setAudio(SetAudioRequest.builder()
                    .url(url)
                    .start(i)
                    .sentence("Sentence #" + i)
                    .build());
    }

}
