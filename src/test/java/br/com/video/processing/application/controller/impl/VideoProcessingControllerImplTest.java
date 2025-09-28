package br.com.video.processing.application.controller.impl;

import br.com.video.processing.application.mapper.RequestVideoInfoMapper;
import br.com.video.processing.application.usecase.ExtractFramesUseCase;
import br.com.video.processing.application.usecase.GetVideoUseCase;
import br.com.video.processing.application.usecase.CompleteChunkUseCase;
import br.com.video.processing.application.usecase.PublishVideoStatusUseCase;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoProcessingControllerImplTest {
    private RequestVideoInfoMapper requestVideoInfoMapper;
    private GetVideoUseCase getVideoUseCase;
    private ExtractFramesUseCase extractFramesUseCase;
    private CompleteChunkUseCase completeChunkUseCase;
    private PublishVideoStatusUseCase publishVideoStatusUseCase;
    private VideoProcessingControllerImpl controller;

    @BeforeEach
    void setUp() {
        requestVideoInfoMapper = mock(RequestVideoInfoMapper.class);
        getVideoUseCase = mock(GetVideoUseCase.class);
        extractFramesUseCase = mock(ExtractFramesUseCase.class);
        completeChunkUseCase = mock(CompleteChunkUseCase.class);
        publishVideoStatusUseCase = mock(PublishVideoStatusUseCase.class);
        controller = new VideoProcessingControllerImpl(
                requestVideoInfoMapper,
                getVideoUseCase,
                extractFramesUseCase,
                completeChunkUseCase,
                publishVideoStatusUseCase
        );
    }

    @Test
    void processVideo_success() {
        UploadedVideoInfoDto dto = mock(UploadedVideoInfoDto.class);
        VideoChunkInfo chunkInfo = mock(VideoChunkInfo.class);
        InputStream videoStream = new ByteArrayInputStream(new byte[]{1,2,3});
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        long videoId = 123L;

        when(requestVideoInfoMapper.requestDtoToDomain(dto)).thenReturn(chunkInfo);
        when(getVideoUseCase.getVideo(chunkInfo)).thenReturn(videoStream);
        when(completeChunkUseCase.onChunkProcessed(chunkInfo)).thenReturn(true); // last chunk
        when(chunkInfo.getUserId()).thenReturn(userId);
        when(chunkInfo.getVideoId()).thenReturn(videoId);

        controller.processVideo(dto);

        InOrder inOrder = Mockito.inOrder(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase, publishVideoStatusUseCase);
        inOrder.verify(requestVideoInfoMapper).requestDtoToDomain(dto);
        inOrder.verify(getVideoUseCase).getVideo(chunkInfo);
        inOrder.verify(extractFramesUseCase).extractAndSave(chunkInfo, videoStream);
        inOrder.verify(completeChunkUseCase).onChunkProcessed(chunkInfo);
        inOrder.verify(publishVideoStatusUseCase).publishStatus(userId, videoId, "SUCCESS");
        verifyNoMoreInteractions(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase, publishVideoStatusUseCase);
    }

    @Test
    void processVideo_nonLastChunk_shouldNotPublishSuccess() {
        UploadedVideoInfoDto dto = mock(UploadedVideoInfoDto.class);
        VideoChunkInfo chunkInfo = mock(VideoChunkInfo.class);
        InputStream videoStream = new ByteArrayInputStream(new byte[]{1,2,3});

        when(requestVideoInfoMapper.requestDtoToDomain(dto)).thenReturn(chunkInfo);
        when(getVideoUseCase.getVideo(chunkInfo)).thenReturn(videoStream);
        when(completeChunkUseCase.onChunkProcessed(chunkInfo)).thenReturn(false); // not last chunk

        controller.processVideo(dto);

        InOrder inOrder = Mockito.inOrder(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase);
        inOrder.verify(requestVideoInfoMapper).requestDtoToDomain(dto);
        inOrder.verify(getVideoUseCase).getVideo(chunkInfo);
        inOrder.verify(extractFramesUseCase).extractAndSave(chunkInfo, videoStream);
        inOrder.verify(completeChunkUseCase).onChunkProcessed(chunkInfo);
        verifyNoInteractions(publishVideoStatusUseCase);
        verifyNoMoreInteractions(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase);
    }

    @Test
    void processVideo_shouldThrowRuntimeException_whenExceptionOccurs() {
        UploadedVideoInfoDto dto = mock(UploadedVideoInfoDto.class);
        VideoChunkInfo chunkInfo = mock(VideoChunkInfo.class);
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        long videoId = 123L;
        when(requestVideoInfoMapper.requestDtoToDomain(dto)).thenReturn(chunkInfo);
        when(getVideoUseCase.getVideo(chunkInfo)).thenThrow(new RuntimeException("erro de video"));
        when(chunkInfo.getUserId()).thenReturn(userId);
        when(chunkInfo.getVideoId()).thenReturn(videoId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.processVideo(dto));
        assertTrue(ex.getMessage().contains("Falha ao processar o vídeo: erro de video"));
        InOrder inOrder = Mockito.inOrder(requestVideoInfoMapper, getVideoUseCase, publishVideoStatusUseCase);
        inOrder.verify(requestVideoInfoMapper).requestDtoToDomain(dto);
        inOrder.verify(getVideoUseCase).getVideo(chunkInfo);
        inOrder.verify(publishVideoStatusUseCase).publishStatus(userId, videoId, "ERROR");
        verifyNoMoreInteractions(requestVideoInfoMapper, getVideoUseCase, extractFramesUseCase, completeChunkUseCase, publishVideoStatusUseCase);
    }
}
