package br.com.video.processing.application.usecase.impl;

import br.com.video.processing.application.gateway.VideoGateway;
import br.com.video.processing.application.usecase.GetVideoUseCase;
import br.com.video.processing.domain.VideoChunkInfo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.util.Optional;

@ApplicationScoped
public class GetVideoUseCaseImpl implements GetVideoUseCase {
    private final VideoGateway videoGateway;

    @Inject
    public GetVideoUseCaseImpl(VideoGateway videoGateway) {
        this.videoGateway = videoGateway;
    }

    @Override
    public InputStream getVideo(VideoChunkInfo videoChunkInfo) {
        Optional<InputStream> videoStream = videoGateway.getVideo(videoChunkInfo);
        return videoStream.orElseThrow(() -> new RuntimeException("Video not found"));
    }
}
