package br.com.video.processing.infrastructure.kafka.serialization;

import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UploadedVideoInfoDtoDeserializerTest {
    @Test
    void returnsNullWhenDataNull() {
        UploadedVideoInfoDtoDeserializer d = new UploadedVideoInfoDtoDeserializer();
        assertNull(d.deserialize("t", null));
    }

    @Test
    void deserializesValidJson() {
        UUID id = UUID.randomUUID();
        UUID user = UUID.randomUUID();
        String json = "{"+
                "\"id\":\""+id+"\","+
                "\"containerName\":\"c\","+
                "\"connectionString\":\"conn\","+
                "\"fileName\":\"f.mp4\","+
                "\"chunkPosition\":1,"+
                "\"totalChunks\":2,"+
                "\"userId\":\""+user+"\""+
                "}";
        UploadedVideoInfoDtoDeserializer d = new UploadedVideoInfoDtoDeserializer();
        UploadedVideoInfoDto dto = d.deserialize("t", json.getBytes(StandardCharsets.UTF_8));
        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("c", dto.getContainerName());
        assertEquals("conn", dto.getConnectionString());
        assertEquals("f.mp4", dto.getFileName());
        assertEquals(1, dto.getChunkPosition());
        assertEquals(2, dto.getTotalChunks());
        assertEquals(user, dto.getUserId());
    }

    @Test
    void throwsRuntimeOnInvalidJson() {
        UploadedVideoInfoDtoDeserializer d = new UploadedVideoInfoDtoDeserializer();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> d.deserialize("t", "{".getBytes(StandardCharsets.UTF_8)));
        assertTrue(ex.getMessage().contains("Failed to deserialize UploadedVideoInfoDto"));
    }
}

