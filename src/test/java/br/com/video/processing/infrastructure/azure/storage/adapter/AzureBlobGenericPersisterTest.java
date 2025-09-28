package br.com.video.processing.infrastructure.azure.storage.adapter;

import br.com.video.processing.infrastructure.azure.storage.factory.AzureBlobServiceClientFactory;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AzureBlobGenericPersisterTest {
    private AzureBlobGenericPersister persister;
    private BlobServiceClient blobServiceClient;
    private BlobContainerClient containerClient;
    private BlobClient blobClient;
    private MockedStatic<AzureBlobServiceClientFactory> factoryMockedStatic;

    @BeforeEach
    void setUp() {
        persister = new AzureBlobGenericPersister();
        blobServiceClient = mock(BlobServiceClient.class);
        containerClient = mock(BlobContainerClient.class);
        blobClient = mock(BlobClient.class);
        factoryMockedStatic = Mockito.mockStatic(AzureBlobServiceClientFactory.class);
        factoryMockedStatic.when(() -> AzureBlobServiceClientFactory.getClient(any())).thenReturn(blobServiceClient);
        when(blobServiceClient.getBlobContainerClient(any())).thenReturn(containerClient);
        when(containerClient.getBlobClient(any())).thenReturn(blobClient);
    }

    @Test
    void testSaveWithContentType() {
        InputStream data = new ByteArrayInputStream(new byte[]{1,2,3});
        persister.save("conn", "container", "blob", "video/mp4", data, 3L);
        verify(containerClient).createIfNotExists();
        verify(blobClient).upload(any(InputStream.class), eq(3L), eq(true));
        ArgumentCaptor<BlobHttpHeaders> captor = ArgumentCaptor.forClass(BlobHttpHeaders.class);
        verify(blobClient).setHttpHeaders(captor.capture());
        assertEquals("video/mp4", captor.getValue().getContentType());
    }

    @Test
    void testSaveWithNullContentType() {
        InputStream data = new ByteArrayInputStream(new byte[]{1});
        persister.save("conn", "container", "blob", null, data, 1L);
        verify(containerClient).createIfNotExists();
        verify(blobClient).upload(any(InputStream.class), eq(1L), eq(true));
        verify(blobClient, never()).setHttpHeaders(any());
    }

    @Test
    void testSaveWithBlankContentType() {
        InputStream data = new ByteArrayInputStream(new byte[]{1});
        persister.save("conn", "container", "blob", "   ", data, 1L);
        verify(containerClient).createIfNotExists();
        verify(blobClient).upload(any(InputStream.class), eq(1L), eq(true));
        verify(blobClient, never()).setHttpHeaders(any());
    }

    @AfterEach
    void tearDown() {
        factoryMockedStatic.close();
    }
}
