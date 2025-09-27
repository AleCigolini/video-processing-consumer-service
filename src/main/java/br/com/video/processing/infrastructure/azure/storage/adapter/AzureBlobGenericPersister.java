package br.com.video.processing.infrastructure.azure.storage.adapter;

import br.com.video.processing.common.interfaces.BlobStoragePersister;
import br.com.video.processing.infrastructure.azure.storage.factory.AzureBlobServiceClientFactory;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;

@ApplicationScoped
public class AzureBlobGenericPersister implements BlobStoragePersister {
    @Override
    public void save(String connectionString, String containerName, String blobPath, String contentType, InputStream data, long length) {
        BlobServiceClient blobServiceClient = AzureBlobServiceClientFactory.getClient(connectionString);
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        containerClient.createIfNotExists();

        BlobClient blobClient = containerClient.getBlobClient(blobPath);
        blobClient.upload(data, length, true);
        if (contentType != null && !contentType.isBlank()) {
            blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
        }
    }
}

