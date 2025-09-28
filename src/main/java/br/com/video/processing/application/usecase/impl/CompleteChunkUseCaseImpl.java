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
    public void onChunkProcessed(VideoChunkInfo info) {
        long count = progressRepository.addPosition(info.getId(), info.getChunkPosition());
        int total = info.getTotalChunks();
        if (count >= total && !progressRepository.isZipDone(info.getId())) {
            if (progressRepository.tryAcquireZipLock(info.getId(), 300)) {
                try {
                    framesZipper.zipFrames(info);
                    progressRepository.markZipDone(info.getId());
                } finally {
                }
            }
        }
    }
}

