package br.com.video.processing.application.controller.impl;

import br.com.video.processing.application.controller.VideoProcessingController;
import br.com.video.processing.application.mapper.RequestVideoInfoMapper;
import br.com.video.processing.application.usecase.ExtractFramesUseCase;
import br.com.video.processing.application.usecase.GetVideoUseCase;
import br.com.video.processing.application.usecase.CompleteChunkUseCase;
import br.com.video.processing.application.usecase.PublishVideoStatusUseCase;
import br.com.video.processing.common.domain.dto.request.UploadedVideoInfoDto;
import br.com.video.processing.domain.VideoChunkInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.InputStream;

@ApplicationScoped
public class VideoProcessingControllerImpl implements VideoProcessingController {
    private static final Logger LOG = Logger.getLogger(VideoProcessingControllerImpl.class);
    RequestVideoInfoMapper requestVideoInfoMapper;
    GetVideoUseCase getVideoUseCase;
    ExtractFramesUseCase extractFramesUseCase;
    CompleteChunkUseCase completeChunkUseCase;
    PublishVideoStatusUseCase publishVideoStatusUseCase;

    @Inject
    public VideoProcessingControllerImpl(
            RequestVideoInfoMapper requestVideoInfoMapper,
            GetVideoUseCase getVideoUseCase,
            ExtractFramesUseCase extractFramesUseCase,
            CompleteChunkUseCase completeChunkUseCase,
            PublishVideoStatusUseCase publishVideoStatusUseCase
    ) {
        this.requestVideoInfoMapper = requestVideoInfoMapper;
        this.getVideoUseCase = getVideoUseCase;
        this.extractFramesUseCase = extractFramesUseCase;
        this.completeChunkUseCase = completeChunkUseCase;
        this.publishVideoStatusUseCase = publishVideoStatusUseCase;
    }

    @Override
    public void processVideo(UploadedVideoInfoDto uploadedVideoInfoDto) {
        VideoChunkInfo videoChunkInfo = requestVideoInfoMapper.requestDtoToDomain(uploadedVideoInfoDto);
        try (InputStream videoStream = getVideoUseCase.getVideo(videoChunkInfo)) {
            extractFramesUseCase.extractAndSave(videoChunkInfo, videoStream);
            final boolean isLastChunk = completeChunkUseCase.onChunkProcessed(videoChunkInfo);
            if (isLastChunk) {
                publishVideoStatusUseCase.publishStatus(videoChunkInfo.getUserId(), videoChunkInfo.getVideoId(), "SUCCESS");
            }
        } catch (Exception e) {
            LOG.errorf(e, "Error processing video for user %s, video %s", videoChunkInfo.getUserId(), videoChunkInfo.getVideoId());
            publishVideoStatusUseCase.publishStatus(videoChunkInfo.getUserId(), videoChunkInfo.getVideoId(), "ERROR");
        }
    }
}
