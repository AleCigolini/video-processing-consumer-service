package br.com.video.processing.infrastructure.azure.storage.adapter;

import br.com.video.processing.common.interfaces.VideoStorageFetcher;
import br.com.video.processing.domain.VideoChunkInfo;
import br.com.video.processing.infrastructure.azure.storage.factory.AzureBlobServiceClientFactory;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.util.Optional;

@ApplicationScoped
public class AzureBlobVideoStorageFetcher implements VideoStorageFetcher {

    @Override
    public Optional<InputStream> fetch(VideoChunkInfo videoChunkInfo) {
        try {
            BlobServiceClient blobServiceClient = AzureBlobServiceClientFactory.getClient(videoChunkInfo.getConnectionString());
            BlobContainerClient containerClient = blobServiceClient
                    .getBlobContainerClient(videoChunkInfo.getContainerName());

            String blobPath = videoChunkInfo.getVideoId() + "/chunks/" + videoChunkInfo.getFileName();

            BlobClient blobClient = containerClient.getBlobClient(blobPath);

            if (!blobClient.exists()) {
                return Optional.empty();
            }

            return Optional.of(blobClient.openInputStream());

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
