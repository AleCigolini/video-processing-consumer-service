package br.com.video.processing.application.mapper;

import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;

public interface RequestVideoInfoMapper {
    VideoChunkInfo requestDtoToDomain(UploadedVideoInfoDto requestDto);
}
