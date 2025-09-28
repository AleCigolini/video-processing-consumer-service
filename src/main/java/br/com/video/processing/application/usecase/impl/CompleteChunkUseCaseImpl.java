package br.com.video.processing.application.usecase.impl;

import br.com.video.processing.application.usecase.CompleteChunkUseCase;
import br.com.video.processing.common.interfaces.ChunkProgressRepository;
import br.com.video.processing.common.interfaces.FramesZipper;
import br.com.video.processing.domain.VideoChunkInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CompleteChunkUseCaseImpl implements CompleteChunkUseCase {

    private final ChunkProgressRepository progressRepository;
    private final FramesZipper framesZipper;

    @Inject
    public CompleteChunkUseCaseImpl(ChunkProgressRepository progressRepository, FramesZipper framesZipper) {
        this.progressRepository = progressRepository;
        this.framesZipper = framesZipper;
    }

    @Override
    public boolean onChunkProcessed(VideoChunkInfo info) {
        long count = progressRepository.addPosition(info.getVideoId(), info.getChunkPosition());
        int total = info.getTotalChunks();
        boolean isLast = count >= total;
        if (isLast && !progressRepository.isZipDone(info.getVideoId())) {
            if (progressRepository.tryAcquireZipLock(info.getVideoId(), 300)) {
                try {
                    framesZipper.zipFrames(info);
                    progressRepository.markZipDone(info.getVideoId());
                } finally {
                }
            }
        }
        return isLast;
    }
}
