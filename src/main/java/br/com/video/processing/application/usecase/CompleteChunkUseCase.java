package br.com.video.processing.application.usecase;

import br.com.video.processing.domain.VideoChunkInfo;

public interface CompleteChunkUseCase {
    void onChunkProcessed(VideoChunkInfo info);
}

