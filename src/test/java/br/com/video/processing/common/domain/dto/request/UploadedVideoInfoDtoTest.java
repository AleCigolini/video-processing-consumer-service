package br.com.video.processing.common.domain.dto.request;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UploadedVideoInfoDtoTest {
    @Test
    void testNoArgsConstructorAndSettersGetters() {
        UploadedVideoInfoDto dto = new UploadedVideoInfoDto();
        UUID id = UUID.randomUUID();
        String containerName = "container";
        String connectionString = "connstr";
        String fileName = "file.mp4";
        Integer chunkPosition = 1;
        Integer totalChunks = 5;
        UUID userId = UUID.randomUUID();

        dto.setId(id);
        dto.setContainerName(containerName);
        dto.setConnectionString(connectionString);
        dto.setFileName(fileName);
        dto.setChunkPosition(chunkPosition);
        dto.setTotalChunks(totalChunks);
        dto.setUserId(userId);

        assertEquals(id, dto.getId());
        assertEquals(containerName, dto.getContainerName());
        assertEquals(connectionString, dto.getConnectionString());
        assertEquals(fileName, dto.getFileName());
        assertEquals(chunkPosition, dto.getChunkPosition());
        assertEquals(totalChunks, dto.getTotalChunks());
        assertEquals(userId, dto.getUserId());
    }

    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String containerName = "container";
        String connectionString = "connstr";
        String fileName = "file.mp4";
        Integer chunkPosition = 2;
        Integer totalChunks = 10;
        UUID userId = UUID.randomUUID();

        UploadedVideoInfoDto dto = new UploadedVideoInfoDto(id, containerName, connectionString, fileName, chunkPosition, totalChunks, userId);

        assertEquals(id, dto.getId());
        assertEquals(containerName, dto.getContainerName());
        assertEquals(connectionString, dto.getConnectionString());
        assertEquals(fileName, dto.getFileName());
        assertEquals(chunkPosition, dto.getChunkPosition());
        assertEquals(totalChunks, dto.getTotalChunks());
        assertEquals(userId, dto.getUserId());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UploadedVideoInfoDto dto1 = new UploadedVideoInfoDto(id, "c", "cs", "f", 1, 2, userId);
        UploadedVideoInfoDto dto2 = new UploadedVideoInfoDto(id, "c", "cs", "f", 1, 2, userId);
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UploadedVideoInfoDto dto = new UploadedVideoInfoDto(id, "c", "cs", "f", 1, 2, userId);
        String str = dto.toString();
        assertTrue(str.contains("id"));
        assertTrue(str.contains("containerName"));
        assertTrue(str.contains("connectionString"));
        assertTrue(str.contains("fileName"));
        assertTrue(str.contains("chunkPosition"));
        assertTrue(str.contains("totalChunks"));
        assertTrue(str.contains("userId"));
    }
}

