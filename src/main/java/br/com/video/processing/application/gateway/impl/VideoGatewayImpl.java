package br.com.video.processing.application.gateway.impl;

import br.com.video.processing.application.gateway.VideoGateway;
import br.com.video.processing.common.interfaces.VideoStorageFetcher;
import br.com.video.processing.domain.VideoChunkInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.InputStream;
import java.util.Optional;

@ApplicationScoped
public class VideoGatewayImpl implements VideoGateway {
    private final VideoStorageFetcher videoStorageFetcher;

    @Inject
    public VideoGatewayImpl(VideoStorageFetcher videoStorageFetcher) {
        this.videoStorageFetcher = videoStorageFetcher;
    }

    @Override
    public Optional<InputStream> getVideo(VideoChunkInfo videoChunkInfo) {
        return videoStorageFetcher.fetch(videoChunkInfo);
    }

}
