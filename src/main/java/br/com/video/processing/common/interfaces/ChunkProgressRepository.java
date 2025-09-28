package br.com.video.processing.common.interfaces;

import java.util.List;

public interface ChunkProgressRepository {
    long addPosition(long videoId, int position);
    long getCount(long videoId);
    List<Integer> getPositions(long videoId);

    boolean isZipDone(long videoId);
    void markZipDone(long videoId);
    boolean tryAcquireZipLock(long videoId, long ttlSeconds);
}
