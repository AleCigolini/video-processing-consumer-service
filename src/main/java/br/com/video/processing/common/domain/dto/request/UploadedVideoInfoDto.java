package br.com.video.processing.common.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UploadedVideoInfoDto {
    private long videoId;
    @NotBlank
    @NotNull
    private String containerName;
    @NotBlank
    @NotNull
    private String connectionString;
    @NotBlank
    @NotNull
    private String fileName;
    @NotNull
    private Integer chunkPosition;
    @NotNull
    private Integer totalChunks;
    @NotNull
    private UUID userId;

    public UploadedVideoInfoDto() {
    }

    public UploadedVideoInfoDto(long videoId, String containerName, String connectionString, String fileName, Integer chunkPosition, Integer totalChunks, UUID userId) {
        this.videoId = videoId;
        this.containerName = containerName;
        this.connectionString = connectionString;
        this.fileName = fileName;
        this.chunkPosition = chunkPosition;
        this.totalChunks = totalChunks;
        this.userId = userId;
    }
}
