package br.com.video.processing.application.gateway.impl;

import br.com.video.processing.common.interfaces.VideoStorageFetcher;
import br.com.video.processing.domain.VideoChunkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoGatewayImplTest {
    private VideoStorageFetcher videoStorageFetcher;
    private VideoGatewayImpl videoGateway;
    private VideoChunkInfo videoChunkInfo;

    @BeforeEach
    void setUp() {
        videoStorageFetcher = mock(VideoStorageFetcher.class);
        videoGateway = new VideoGatewayImpl(videoStorageFetcher);
        videoChunkInfo = mock(VideoChunkInfo.class);
    }

    @Test
    void getVideo_shouldReturnOptionalWithStream_whenFetcherReturnsStream() {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1,2,3});
        when(videoStorageFetcher.fetch(videoChunkInfo)).thenReturn(Optional.of(inputStream));

        Optional<InputStream> result = videoGateway.getVideo(videoChunkInfo);

        assertTrue(result.isPresent());
        assertEquals(inputStream, result.get());
        verify(videoStorageFetcher).fetch(videoChunkInfo);
        verifyNoMoreInteractions(videoStorageFetcher);
    }

    @Test
    void getVideo_shouldReturnEmptyOptional_whenFetcherReturnsEmpty() {
        when(videoStorageFetcher.fetch(videoChunkInfo)).thenReturn(Optional.empty());

        Optional<InputStream> result = videoGateway.getVideo(videoChunkInfo);

        assertFalse(result.isPresent());
        verify(videoStorageFetcher).fetch(videoChunkInfo);
        verifyNoMoreInteractions(videoStorageFetcher);
    }
}

