package br.com.video.processing.application.controller.impl;

import br.com.video.processing.application.mapper.RequestVideoInfoMapper;
import br.com.video.processing.application.usecase.ExtractFramesUseCase;
import br.com.video.processing.application.usecase.GetVideoUseCase;
import br.com.video.processing.application.usecase.CompleteChunkUseCase;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoProcessingControllerImplTest {
    private RequestVideoInfoMapper requestVideoInfoMapper;
    private GetVideoUseCase getVideoUseCase;
    private ExtractFramesUseCase extractFramesUseCase;
    private CompleteChunkUseCase completeChunkUseCase;
    private VideoProcessingControllerImpl controller;

    @BeforeEach
    void setUp() {
        requestVideoInfoMapper = mock(RequestVideoInfoMapper.class);
        getVideoUseCase = mock(GetVideoUseCase.class);
        extractFramesUseCase = mock(ExtractFramesUseCase.class);
        completeChunkUseCase = mock(CompleteChunkUseCase.class);
        controller = new VideoProcessingControllerImpl(
                requestVideoInfoMapper,
                getVideoUseCase,
                extractFramesUseCase,
                completeChunkUseCase
        );
    }

    @Test
    void processVideo_success() {
        UploadedVideoInfoDto dto = mock(UploadedVideoInfoDto.class);
        VideoChunkInfo chunkInfo = mock(VideoChunkInfo.class);
        InputStream videoStream = new ByteArrayInputStream(new byte[]{1,2,3});

        when(requestVideoInfoMapper.requestDtoToDomain(dto)).thenReturn(chunkInfo);
        when(getVideoUseCase.getVideo(chunkInfo)).thenReturn(videoStream);

        controller.processVideo(dto);

        InOrder inOrder = Mockito.inOrder(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase);
        inOrder.verify(requestVideoInfoMapper).requestDtoToDomain(dto);
        inOrder.verify(getVideoUseCase).getVideo(chunkInfo);
        inOrder.verify(extractFramesUseCase).extractAndSave(chunkInfo, videoStream);
        inOrder.verify(completeChunkUseCase).onChunkProcessed(chunkInfo);
        verifyNoMoreInteractions(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase);
    }

    @Test
    void processVideo_shouldThrowRuntimeException_whenExceptionOccurs() {
        UploadedVideoInfoDto dto = mock(UploadedVideoInfoDto.class);
        VideoChunkInfo chunkInfo = mock(VideoChunkInfo.class);
        when(requestVideoInfoMapper.requestDtoToDomain(dto)).thenReturn(chunkInfo);
        when(getVideoUseCase.getVideo(chunkInfo)).thenThrow(new RuntimeException("erro de video"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.processVideo(dto));
        assertTrue(ex.getMessage().contains("Falha ao processar o vídeo: erro de video"));
        verify(requestVideoInfoMapper).requestDtoToDomain(dto);
        verify(getVideoUseCase).getVideo(chunkInfo);
        verifyNoMoreInteractions(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase);
    }
}
