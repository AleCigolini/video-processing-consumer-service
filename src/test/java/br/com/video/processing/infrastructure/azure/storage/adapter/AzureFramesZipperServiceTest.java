package br.com.video.processing.infrastructure.azure.storage.adapter;

import br.com.video.processing.common.interfaces.BlobStoragePersister;
import br.com.video.processing.domain.VideoChunkInfo;
import br.com.video.processing.infrastructure.azure.storage.factory.AzureBlobServiceClientFactory;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.specialized.BlobInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AzureFramesZipperServiceTest {
    BlobStoragePersister persister;
    AzureFramesZipperService service;
    BlobServiceClient blobServiceClient;
    BlobContainerClient containerClient;
    BlobClient blobClient;
    MockedStatic<AzureBlobServiceClientFactory> factoryMockedStatic;
    VideoChunkInfo info;
    UUID userId;
    long videoId;

    @BeforeEach
    void setUp() {
        persister = mock(BlobStoragePersister.class);
        service = new AzureFramesZipperService(persister);
        blobServiceClient = mock(BlobServiceClient.class);
        containerClient = mock(BlobContainerClient.class);
        blobClient = mock(BlobClient.class);
        factoryMockedStatic = Mockito.mockStatic(AzureBlobServiceClientFactory.class);
        factoryMockedStatic.when(() -> AzureBlobServiceClientFactory.getClient(any())).thenReturn(blobServiceClient);
        when(blobServiceClient.getBlobContainerClient(any())).thenReturn(containerClient);
        info = mock(VideoChunkInfo.class);
        userId = UUID.randomUUID();
        videoId = 12345L;
        when(info.getConnectionString()).thenReturn("conn");
        when(info.getContainerName()).thenReturn("container");
        when(info.getUserId()).thenReturn(userId);
        when(info.getVideoId()).thenReturn(videoId);
    }

    @AfterEach
    void tearDown() {
        factoryMockedStatic.close();
    }

    @Test
    void testZipFramesWithPngs() throws Exception {
        String prefix = userId + "/" + videoId + "/";
        BlobItem pngItem = mock(BlobItem.class);
        when(pngItem.getName()).thenReturn(prefix + "frame1.png");
        BlobItem nonPngItem = mock(BlobItem.class);
        when(nonPngItem.getName()).thenReturn(prefix + "file.txt");
        Iterable<BlobItem> iterable = java.util.Arrays.asList(pngItem, nonPngItem);
        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(iterable.iterator());
        when(containerClient.listBlobs(any(ListBlobsOptions.class), any())).thenReturn(pagedIterable);
        when(containerClient.getBlobClient(eq(prefix + "frame1.png"))).thenReturn(blobClient);
        BlobInputStream blobInputStream = mock(BlobInputStream.class);
        when(blobInputStream.read(any(byte[].class), anyInt(), anyInt())).thenReturn(-1);
        when(blobClient.openInputStream()).thenReturn(blobInputStream);
        doAnswer(invocation -> null).when(persister).save(any(), any(), any(), any(), any(), anyLong());
        service.zipFrames(info);
        verify(persister).save(any(), any(), any(), eq("application/zip"), any(), anyLong());
    }

    @Test
    void testZipFramesNoPngs() {
        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(java.util.Collections.emptyIterator());
        when(containerClient.listBlobs(any(ListBlobsOptions.class), any())).thenReturn(pagedIterable);
        doAnswer(invocation -> null).when(persister).save(any(), any(), any(), any(), any(), anyLong());
        service.zipFrames(info);
        verify(persister).save(any(), any(), any(), eq("application/zip"), any(), anyLong());
    }

    @Test
    void testZipFramesThrows() throws Exception {
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class, Mockito.CALLS_REAL_METHODS)) {
            filesMock.when(() -> Files.createTempFile(any(), any())).thenThrow(new RuntimeException("fail temp"));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> service.zipFrames(info));
            assertTrue(ex.getMessage().contains("Falha ao zipar frames"));
        }
    }

    @Test
    void testZipFramesDeleteTempFile() throws Exception {
        Path tmp = Files.createTempFile("frames-test", ".zip");
        tmp.toFile().deleteOnExit();
        String prefix = userId + "/" + videoId + "/";
        BlobItem pngItem = mock(BlobItem.class);
        when(pngItem.getName()).thenReturn(prefix + "frame1.png");
        Iterable<BlobItem> iterable = java.util.Arrays.asList(pngItem);
        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        when(pagedIterable.iterator()).thenReturn(iterable.iterator());
        when(containerClient.listBlobs(any(ListBlobsOptions.class), any())).thenReturn(pagedIterable);
        when(containerClient.getBlobClient(any())).thenReturn(blobClient);
        BlobInputStream blobInputStream = mock(BlobInputStream.class);
        when(blobInputStream.read(any(byte[].class), anyInt(), anyInt())).thenReturn(-1);
        when(blobClient.openInputStream()).thenReturn(blobInputStream);
        doAnswer(invocation -> null).when(persister).save(any(), any(), any(), any(), any(), anyLong());
        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class, Mockito.CALLS_REAL_METHODS)) {
            filesMock.when(() -> Files.createTempFile(any(), any())).thenReturn(tmp);
            filesMock.when(() -> Files.size(tmp)).thenReturn(3L);
            filesMock.when(() -> Files.deleteIfExists(tmp)).then(inv -> true);
            service.zipFrames(info);
            filesMock.verify(() -> Files.deleteIfExists(tmp), Mockito.atLeastOnce());
        }
    }
}
