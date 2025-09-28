package br.com.video.processing.presentation.kafka.impl;

import br.com.video.processing.application.controller.VideoProcessingController;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoProcessingConsumerImplTest {
    @Test
    void consumeDelegatesToController() {
        VideoProcessingController controller = mock(VideoProcessingController.class);
        VideoProcessingConsumerImpl consumer = new VideoProcessingConsumerImpl(controller);
        UploadedVideoInfoDto dto = new UploadedVideoInfoDto(
                UUID.randomUUID(),
                "container",
                "UseDevelopmentStorage=true",
                "file.mp4",
                1,
                2,
                UUID.randomUUID()
        );
        consumer.consume(dto);
        ArgumentCaptor<UploadedVideoInfoDto> captor = ArgumentCaptor.forClass(UploadedVideoInfoDto.class);
        verify(controller).processVideo(captor.capture());
        assertEquals(dto, captor.getValue());
    }
}

