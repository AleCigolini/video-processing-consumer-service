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
        when(info.getId()).thenReturn(java.util.UUID.randomUUID());
        when(info.getChunkPosition()).thenReturn(1);
        when(info.getTotalChunks()).thenReturn(2);
    }

    @Test
    void onChunkProcessed_shouldNotZip_whenCountLessThanTotal() {
        when(progressRepository.addPosition(any(), anyInt())).thenReturn(1L);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(info.getId(), info.getChunkPosition());
        verify(info).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }

    @Test
    void onChunkProcessed_shouldNotZip_whenZipAlreadyDone() {
        when(progressRepository.addPosition(any(), anyInt())).thenReturn(2L);
        when(progressRepository.isZipDone(any())).thenReturn(true);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(info.getId(), info.getChunkPosition());
        verify(progressRepository).isZipDone(info.getId());
        verify(info).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }

    @Test
    void onChunkProcessed_shouldNotZip_whenLockNotAcquired() {
        when(progressRepository.addPosition(any(), anyInt())).thenReturn(2L);
        when(progressRepository.isZipDone(any())).thenReturn(false);
        when(progressRepository.tryAcquireZipLock(any(), anyInt())).thenReturn(false);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(info.getId(), info.getChunkPosition());
        verify(progressRepository).isZipDone(info.getId());
        verify(progressRepository).tryAcquireZipLock(info.getId(), 300);
        verify(info).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }

    @Test
    void onChunkProcessed_shouldZipAndMarkDone_whenAllConditionsMet() {
        java.util.UUID uuid = java.util.UUID.randomUUID();
        when(info.getId()).thenReturn(uuid);
        when(info.getChunkPosition()).thenReturn(2);
        when(info.getTotalChunks()).thenReturn(2);
        when(progressRepository.addPosition(uuid, 2)).thenReturn(2L);
        when(progressRepository.isZipDone(uuid)).thenReturn(false);
        when(progressRepository.tryAcquireZipLock(uuid, 300)).thenReturn(true);
        useCase.onChunkProcessed(info);
        verify(progressRepository).addPosition(uuid, 2);
        verify(progressRepository).isZipDone(uuid);
        verify(progressRepository).tryAcquireZipLock(uuid, 300);
        verify(framesZipper).zipFrames(info);
        verify(progressRepository).markZipDone(uuid);
        verify(info, atLeastOnce()).getTotalChunks();
        verifyNoMoreInteractions(progressRepository, framesZipper);
    }
}
