package br.com.video.processing.infrastructure.azure.storage.factory;

import com.azure.storage.blob.BlobServiceClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AzureBlobServiceClientFactoryTest {
    @Test
    void returnsSameInstanceForSameConnectionString() {
        String cs = "DefaultEndpointsProtocol=https;AccountName=testacc;AccountKey=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=;EndpointSuffix=core.windows.net";
        BlobServiceClient c1 = AzureBlobServiceClientFactory.getClient(cs);
        BlobServiceClient c2 = AzureBlobServiceClientFactory.getClient(cs);
        assertNotNull(c1);
        assertSame(c1, c2);
    }

    @Test
    void returnsDifferentInstancesForDifferentConnectionStrings() {
        String cs1 = "DefaultEndpointsProtocol=https;AccountName=a1;AccountKey=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=;EndpointSuffix=core.windows.net";
        String cs2 = "DefaultEndpointsProtocol=https;AccountName=a2;AccountKey=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=;EndpointSuffix=core.windows.net";
        BlobServiceClient c1 = AzureBlobServiceClientFactory.getClient(cs1);
        BlobServiceClient c2 = AzureBlobServiceClientFactory.getClient(cs2);
        assertNotNull(c1);
        assertNotNull(c2);
        assertNotSame(c1, c2);
    }
}

