package br.com.video.processing.domain;

import java.util.UUID;

public class VideoChunkInfo {
    private final UUID id;
    private final UUID userId;
    private final String containerName;
    private final String connectionString;
    private final String fileName;
    private final int chunkPosition;
    private final int totalChunks;

    public VideoChunkInfo(UUID id, UUID userId, String containerName, String connectionString, String fileName, int chunkPosition, int totalChunks) {
        this.id = id;
        this.userId = userId;
        this.containerName = containerName;
        this.connectionString = connectionString;
        this.fileName = fileName;
        this.chunkPosition = chunkPosition;
        this.totalChunks = totalChunks;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getFileName() {
        return fileName;
    }

    public int getChunkPosition() {
        return chunkPosition;
    }

    public int getTotalChunks() {
        return totalChunks;
    }
}
