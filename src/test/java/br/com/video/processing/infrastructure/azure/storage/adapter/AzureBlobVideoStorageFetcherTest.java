package br.com.video.processing.infrastructure.azure.storage.adapter;

import br.com.video.processing.domain.VideoChunkInfo;
import br.com.video.processing.infrastructure.azure.storage.factory.AzureBlobServiceClientFactory;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.specialized.BlobInputStream;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AzureBlobVideoStorageFetcherTest {

    @Test
    void fetch_shouldReturnStream_whenBlobExists() {
        VideoChunkInfo info = mock(VideoChunkInfo.class);
        when(info.getConnectionString()).thenReturn("cs");
        when(info.getContainerName()).thenReturn("container");
        when(info.getVideoId()).thenReturn(777L);
        when(info.getFileName()).thenReturn("file.mp4");

        BlobServiceClient service = mock(BlobServiceClient.class);
        BlobContainerClient container = mock(BlobContainerClient.class);
        BlobClient blobClient = mock(BlobClient.class);

        try (MockedStatic<AzureBlobServiceClientFactory> factory = mockStatic(AzureBlobServiceClientFactory.class)) {
            factory.when(() -> AzureBlobServiceClientFactory.getClient("cs")).thenReturn(service);
            when(service.getBlobContainerClient("container")).thenReturn(container);
            when(container.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.exists()).thenReturn(true);
            BlobInputStream is = mock(BlobInputStream.class);
            when(blobClient.openInputStream()).thenReturn(is);

            AzureBlobVideoStorageFetcher fetcher = new AzureBlobVideoStorageFetcher();
            Optional<InputStream> result = fetcher.fetch(info);

            assertTrue(result.isPresent());
            assertSame(is, result.get());
        }
    }

    @Test
    void fetch_shouldReturnEmpty_whenBlobNotExists() {
        VideoChunkInfo info = mock(VideoChunkInfo.class);
        when(info.getConnectionString()).thenReturn("cs");
        when(info.getContainerName()).thenReturn("container");
        when(info.getVideoId()).thenReturn(888L);
        when(info.getFileName()).thenReturn("file.mp4");

        BlobServiceClient service = mock(BlobServiceClient.class);
        BlobContainerClient container = mock(BlobContainerClient.class);
        BlobClient blobClient = mock(BlobClient.class);

        try (MockedStatic<AzureBlobServiceClientFactory> factory = mockStatic(AzureBlobServiceClientFactory.class)) {
            factory.when(() -> AzureBlobServiceClientFactory.getClient("cs")).thenReturn(service);
            when(service.getBlobContainerClient("container")).thenReturn(container);
            when(container.getBlobClient(anyString())).thenReturn(blobClient);
            when(blobClient.exists()).thenReturn(false);

            AzureBlobVideoStorageFetcher fetcher = new AzureBlobVideoStorageFetcher();
            Optional<InputStream> result = fetcher.fetch(info);

            assertTrue(result.isEmpty());
        }
    }

    @Test
    void fetch_shouldReturnEmpty_onException() {
        VideoChunkInfo info = mock(VideoChunkInfo.class);
        when(info.getConnectionString()).thenReturn("cs");
        when(info.getContainerName()).thenReturn("container");
        when(info.getVideoId()).thenReturn(999L);
        when(info.getFileName()).thenReturn("file.mp4");

        try (MockedStatic<AzureBlobServiceClientFactory> factory = mockStatic(AzureBlobServiceClientFactory.class)) {
            factory.when(() -> AzureBlobServiceClientFactory.getClient("cs")).thenThrow(new RuntimeException("boom"));

            AzureBlobVideoStorageFetcher fetcher = new AzureBlobVideoStorageFetcher();
            Optional<InputStream> result = fetcher.fetch(info);

            assertTrue(result.isEmpty());
        }
    }
}
