package br.com.video.processing.common.interfaces;

import java.util.List;
import java.util.UUID;

public interface ChunkProgressRepository {
    long addPosition(UUID videoId, int position);
    long getCount(UUID videoId);
    List<Integer> getPositions(UUID videoId);

    boolean isZipDone(UUID videoId);
    void markZipDone(UUID videoId);
    boolean tryAcquireZipLock(UUID videoId, long ttlSeconds);
}

