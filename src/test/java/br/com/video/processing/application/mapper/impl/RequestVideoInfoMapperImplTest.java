package br.com.video.processing.application.mapper.impl;

import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RequestVideoInfoMapperImplTest {
    private RequestVideoInfoMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new RequestVideoInfoMapperImpl(new ModelMapper());
    }

    @Test
    void requestDtoToDomain_shouldMapAllFields() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UploadedVideoInfoDto dto = new UploadedVideoInfoDto();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setContainerName("container");
        dto.setConnectionString("connstr");
        dto.setFileName("file.mp4");
        dto.setChunkPosition(5);
        dto.setTotalChunks(10);

        VideoChunkInfo info = mapper.requestDtoToDomain(dto);
        assertEquals(id, info.getId());
        assertEquals(userId, info.getUserId());
        assertEquals("container", info.getContainerName());
        assertEquals("connstr", info.getConnectionString());
        assertEquals("file.mp4", info.getFileName());
        assertEquals(5, info.getChunkPosition());
        assertEquals(10, info.getTotalChunks());
    }

    @Test
    void requestDtoToDomain_shouldDefaultNullChunkFieldsToZero() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UploadedVideoInfoDto dto = new UploadedVideoInfoDto();
        dto.setId(id);
        dto.setUserId(userId);
        dto.setContainerName("cont");
        dto.setConnectionString("cs");
        dto.setFileName("f.mp4");
        dto.setChunkPosition(null);
        dto.setTotalChunks(null);

        VideoChunkInfo info = mapper.requestDtoToDomain(dto);
        assertEquals(0, info.getChunkPosition());
        assertEquals(0, info.getTotalChunks());
    }
}
