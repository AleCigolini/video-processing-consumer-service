package br.com.video.processing.presentation.kafka.serialization;

import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

public class UploadedVideoInfoDtoDeserializer implements Deserializer<UploadedVideoInfoDto> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UploadedVideoInfoDto deserialize(String topic, byte[] data) {
        if (data == null) return null;
        try {
            return objectMapper.readValue(data, UploadedVideoInfoDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize UploadedVideoInfoDto", e);
        }
    }

}

