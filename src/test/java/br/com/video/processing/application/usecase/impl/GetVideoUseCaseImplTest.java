package br.com.video.processing.application.usecase.impl;

import br.com.video.processing.application.gateway.VideoGateway;
import br.com.video.processing.domain.VideoChunkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetVideoUseCaseImplTest {
    private VideoGateway videoGateway;
    private GetVideoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        videoGateway = mock(VideoGateway.class);
        useCase = new GetVideoUseCaseImpl(videoGateway);
    }

    @Test
    void getVideo_shouldReturnStream_whenPresent() {
        VideoChunkInfo info = mock(VideoChunkInfo.class);
        InputStream stream = new ByteArrayInputStream(new byte[]{1});
        when(videoGateway.getVideo(info)).thenReturn(Optional.of(stream));

        InputStream result = useCase.getVideo(info);
        assertSame(stream, result);
        verify(videoGateway).getVideo(info);
    }

    @Test
    void getVideo_shouldThrow_whenEmpty() {
        VideoChunkInfo info = mock(VideoChunkInfo.class);
        when(videoGateway.getVideo(info)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> useCase.getVideo(info));
        assertTrue(ex.getMessage().contains("Video not found"));
        verify(videoGateway).getVideo(info);
    }
}

