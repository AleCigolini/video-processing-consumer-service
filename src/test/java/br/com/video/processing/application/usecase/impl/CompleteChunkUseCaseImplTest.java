package br.com.video.processing.application.usecase.impl;

import br.com.video.processing.common.interfaces.ChunkProgressRepository;
import br.com.video.processing.common.interfaces.FramesZipper;
import br.com.video.processing.domain.VideoChunkInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class CompleteChunkUseCaseImplTest {
    private ChunkProgressRepository progressRepository;
    private FramesZipper framesZipper;
    private CompleteChunkUseCaseImpl useCase;
    private VideoChunkInfo info;

    @BeforeEach
    void setUp() {
        progressRepository = mock(ChunkProgressRepository.class);
        framesZipper = mock(FramesZipper.class);
        useCase = new CompleteChunkUseCaseImpl(progressRepository, framesZipper);
        info = mock(VideoChunkInfo.class);
        when(info.getVideoId()).thenReturn(123L);
        when(info.getChunkPosition()).thenReturn(1);
        when(info.getTotalChunks()).thenReturn(2);
    }

    @Test
    void onChunkProcessed_shouldNotZip_whenCountLessThanTotal() {
        when(progressRepository.addPosition(anyLong(), anyInt())).thenReturn(1L);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(info.getVideoId(), info.getChunkPosition());
        verify(info).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }

    @Test
    void onChunkProcessed_shouldNotZip_whenZipAlreadyDone() {
        when(progressRepository.addPosition(anyLong(), anyInt())).thenReturn(2L);
        when(progressRepository.isZipDone(anyLong())).thenReturn(true);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(info.getVideoId(), info.getChunkPosition());
        verify(progressRepository).isZipDone(info.getVideoId());
        verify(info).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }

    @Test
    void onChunkProcessed_shouldNotZip_whenLockNotAcquired() {
        when(progressRepository.addPosition(anyLong(), anyInt())).thenReturn(2L);
        when(progressRepository.isZipDone(anyLong())).thenReturn(false);
        when(progressRepository.tryAcquireZipLock(anyLong(), anyLong())).thenReturn(false);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(info.getVideoId(), info.getChunkPosition());
        verify(progressRepository).isZipDone(info.getVideoId());
        verify(progressRepository).tryAcquireZipLock(info.getVideoId(), 300);
        verify(info).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }

    @Test
    void onChunkProcessed_shouldZipAndMarkDone_whenAllConditionsMet() {
        long id = 999L;
        when(info.getVideoId()).thenReturn(id);
        when(info.getChunkPosition()).thenReturn(2);
        when(info.getTotalChunks()).thenReturn(2);
        when(progressRepository.addPosition(id, 2)).thenReturn(2L);
        when(progressRepository.isZipDone(id)).thenReturn(false);
        when(progressRepository.tryAcquireZipLock(id, 300)).thenReturn(true);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(id, 2);
        verify(progressRepository).isZipDone(id);
        verify(progressRepository).tryAcquireZipLock(id, 300);
        verify(framesZipper).zipFrames(info);
        verify(progressRepository).markZipDone(id);
        verify(info, atLeastOnce()).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }
}
