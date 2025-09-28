package br.com.video.processing.core.mapper;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

class ModelMapperConfigTest {
    @Test
    void testModelMapperProducer() {
        ModelMapperConfig config = new ModelMapperConfig();
        ModelMapper mapper1 = config.modelMapper();
        ModelMapper mapper2 = config.modelMapper();
        assertNotNull(mapper1);
        assertNotNull(mapper2);
        assertNotSame(mapper1, mapper2);
    }
}

