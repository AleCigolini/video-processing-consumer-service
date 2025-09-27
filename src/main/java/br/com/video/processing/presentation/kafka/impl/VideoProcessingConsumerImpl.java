package br.com.video.processing.presentation.kafka.impl;

import br.com.video.processing.application.controller.VideoProcessingController;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.presentation.kafka.VideoProcessingConsumer;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
@RequiredArgsConstructor
public class VideoProcessingConsumerImpl implements VideoProcessingConsumer {
    private final VideoProcessingController videoProcessingController;

    @Incoming("video-splitted")
    public void consume(UploadedVideoInfoDto uploadedVideoInfoDto) {
        videoProcessingController.processVideo(uploadedVideoInfoDto);
    }

}
