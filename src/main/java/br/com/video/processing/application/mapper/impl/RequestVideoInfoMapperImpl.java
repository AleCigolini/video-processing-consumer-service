package br.com.video.processing.application.mapper.impl;

import br.com.video.processing.application.mapper.RequestVideoInfoMapper;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

@Singleton
public class RequestVideoInfoMapperImpl implements RequestVideoInfoMapper {
    private final ModelMapper modelMapper;

    @Inject
    public RequestVideoInfoMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        Converter<UploadedVideoInfoDto, VideoChunkInfo> converter = new Converter<>() {
            @Override
            public VideoChunkInfo convert(MappingContext<UploadedVideoInfoDto, VideoChunkInfo> context) {
                UploadedVideoInfoDto src = context.getSource();
                return new VideoChunkInfo(
                        src.getId(),
                        src.getUserId(),
                        src.getContainerName(),
                        src.getConnectionString(),
                        src.getFileName(),
                        src.getChunkPosition() == null ? 0 : src.getChunkPosition(),
                        src.getTotalChunks() == null ? 0 : src.getTotalChunks()
                );
            }
        };

        this.modelMapper.addConverter(converter, UploadedVideoInfoDto.class, VideoChunkInfo.class);
    }

    @Override
    public VideoChunkInfo requestDtoToDomain(UploadedVideoInfoDto requestDto) {
        return modelMapper.map(requestDto, VideoChunkInfo.class);
    }
}
