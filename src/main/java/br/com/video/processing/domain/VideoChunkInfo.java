package br.com.video.processing.domain;

import java.util.UUID;

public class VideoChunkInfo {
    private final long videoId;
    private final UUID userId;
    private final String containerName;
    private final String connectionString;
    private final String fileName;
    private final int chunkPosition;
    private final int totalChunks;

    public VideoChunkInfo(long videoId, UUID userId, String containerName, String connectionString, String fileName, int chunkPosition, int totalChunks) {
        this.videoId = videoId;
        this.userId = userId;
        this.containerName = containerName;
        this.connectionString = connectionString;
        this.fileName = fileName;
        this.chunkPosition = chunkPosition;
        this.totalChunks = totalChunks;
    }

    public long getVideoId() {
        return videoId;
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
