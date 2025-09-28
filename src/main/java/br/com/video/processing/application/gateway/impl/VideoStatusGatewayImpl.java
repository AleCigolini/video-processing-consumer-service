package br.com.video.processing.application.gateway.impl;

import br.com.video.processing.application.gateway.VideoStatusGateway;
import br.com.video.processing.infrastructure.kafka.VideoStatusProducer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.UUID;

@ApplicationScoped
public class VideoStatusGatewayImpl implements VideoStatusGateway {
    private final VideoStatusProducer videoStatusProducer;

    @Inject
    public VideoStatusGatewayImpl(VideoStatusProducer videoStatusProducer) {
        this.videoStatusProducer = videoStatusProducer;
    }

    @Override
    public void publishStatus(UUID userId, Long videoId, String status) {
        videoStatusProducer.publishStatus(userId, videoId, status);
    }
}