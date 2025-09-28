package br.com.video.processing.infrastructure.azure.storage.adapter;

import br.com.video.processing.common.interfaces.BlobStoragePersister;
import br.com.video.processing.common.interfaces.FramesZipper;
import br.com.video.processing.domain.VideoChunkInfo;
import br.com.video.processing.infrastructure.azure.storage.factory.AzureBlobServiceClientFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
public class AzureFramesZipperService implements FramesZipper {

    private final BlobStoragePersister persister;

    @Inject
    public AzureFramesZipperService(BlobStoragePersister persister) {
        this.persister = persister;
    }
    
    @Override
    public void zipFrames(VideoChunkInfo info) {
        Path tmpZip = null;
        try {
            tmpZip = Files.createTempFile("frames-" + info.getVideoId(), ".zip");
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(tmpZip))) {
                BlobServiceClient service = AzureBlobServiceClientFactory.getClient(info.getConnectionString());
                BlobContainerClient container = service.getBlobContainerClient(info.getContainerName());
                String prefix = info.getUserId() + "/" + info.getVideoId() + "/";

                for (BlobItem item : container.listBlobs(new ListBlobsOptions().setPrefix(prefix), null)) {
                    String name = item.getName();
                    if (!name.endsWith(".png")) {
                        continue;
                    }
                    BlobClient blobClient = container.getBlobClient(name);
                    try (InputStream in = blobClient.openInputStream()) {
                        String entryName = name.substring(prefix.length());
                        zos.putNextEntry(new ZipEntry(entryName));
                        in.transferTo(zos);
                        zos.closeEntry();
                    }
                }
            }

            removeChunks(info);
            String zipBlobPath = "zip-id-video/" + info.getVideoId() + ".zip";
            try (FileInputStream fis = new FileInputStream(tmpZip.toFile())) {
                persister.save(
                        info.getConnectionString(),
                        info.getContainerName(),
                        zipBlobPath,
                        "application/zip",
                        fis,
                        Files.size(tmpZip)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao zipar frames: " + e.getMessage(), e);
        } finally {
            if (Objects.nonNull(tmpZip)) {
                try { Files.deleteIfExists(tmpZip); } catch (IOException ignored) {}
            }
        }
    }

    private void removeChunks(VideoChunkInfo info) {
        BlobServiceClient service = AzureBlobServiceClientFactory.getClient(info.getConnectionString());
        BlobContainerClient container = service.getBlobContainerClient(info.getContainerName());
        String userVideoPrefix = info.getUserId() + "/" + info.getVideoId() + "/";
        String onlyVideoPrefix = info.getVideoId() + "/";
        deleteByPrefix(container, userVideoPrefix);
        deleteByPrefix(container, onlyVideoPrefix);
    }

    private void deleteByPrefix(BlobContainerClient container, String prefix) {
        try {
            for (BlobItem item : container.listBlobs(new ListBlobsOptions().setPrefix(prefix), null)) {
                try {
                    container.getBlobClient(item.getName()).delete();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }
}
