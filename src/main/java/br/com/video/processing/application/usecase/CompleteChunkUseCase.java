package br.com.video.processing.application.usecase;

import br.com.video.processing.domain.VideoChunkInfo;

public interface CompleteChunkUseCase {
    boolean onChunkProcessed(VideoChunkInfo info);
}
