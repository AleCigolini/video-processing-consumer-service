package br.com.video.processing.application.controller.impl;

import br.com.video.processing.application.controller.VideoProcessingController;
import br.com.video.processing.application.mapper.RequestVideoInfoMapper;
import br.com.video.processing.application.usecase.ExtractFramesUseCase;
import br.com.video.processing.application.usecase.GetVideoUseCase;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;

@ApplicationScoped
public class VideoProcessingControllerImpl implements VideoProcessingController {
    RequestVideoInfoMapper requestVideoInfoMapper;
    GetVideoUseCase getVideoUseCase;
    ExtractFramesUseCase extractFramesUseCase;

    @Inject
    public VideoProcessingControllerImpl(RequestVideoInfoMapper requestVideoInfoMapper, GetVideoUseCase getVideoUseCase, ExtractFramesUseCase extractFramesUseCase) {
        this.requestVideoInfoMapper = requestVideoInfoMapper;
        this.getVideoUseCase = getVideoUseCase;
        this.extractFramesUseCase = extractFramesUseCase;
    }

    @Override
    public void processVideo(UploadedVideoInfoDto uploadedVideoInfoDto) {
        VideoChunkInfo videoChunkInfo = requestVideoInfoMapper.requestDtoToDomain(uploadedVideoInfoDto);
        try (InputStream videoStream = getVideoUseCase.getVideo(videoChunkInfo)) {
            extractFramesUseCase.extractAndSave(videoChunkInfo, videoStream);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar o vídeo: " + e.getMessage(), e);
        }
    }
}
