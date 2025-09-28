package br.com.video.processing.infrastructure.redis;

import br.com.video.processing.common.interfaces.ChunkProgressRepository;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RedisChunkProgressRepositoryTest {
    RedisDataSource ds;
    ListCommands<String, String> list;
    SetCommands<String, String> set;
    ValueCommands<String, String> value;
    KeyCommands<String> keys;
    ChunkProgressRepository repo;
    long videoId;

    @BeforeEach
    void init() {
        ds = mock(RedisDataSource.class);
        list = mock(ListCommands.class);
        set = mock(SetCommands.class);
        value = mock(ValueCommands.class);
        keys = mock(KeyCommands.class);
        when(ds.list(String.class)).thenReturn(list);
        when(ds.set(String.class)).thenReturn(set);
        when(ds.value(String.class)).thenReturn(value);
        when(ds.key()).thenReturn(keys);
        repo = new RedisChunkProgressRepository(ds);
        videoId = 555L;
    }

    @Test
    void addPositionAddsToListWhenNew() {
        when(set.sadd(any(), any())).thenReturn(1);
        when(set.scard(any())).thenReturn(1L);
        long count = repo.addPosition(videoId, 3);
        assertEquals(1L, count);
        verify(list).rpush("video:" + videoId + ":positions", "3");
    }

    @Test
    void addPositionSkipsListWhenExisting() {
        when(set.sadd(any(), any())).thenReturn(0);
        when(set.scard(any())).thenReturn(2L);
        long count = repo.addPosition(videoId, 7);
        assertEquals(2L, count);
        verify(list, never()).rpush(any(), any());
    }

    @Test
    void getCountReturnsSetCardinality() {
        when(set.scard(any())).thenReturn(5L);
        assertEquals(5L, repo.getCount(videoId));
    }

    @Test
    void getPositionsParsesList() {
        when(list.lrange(any(), eq(0L), eq(-1L))).thenReturn(List.of("1", "3", "10"));
        assertEquals(List.of(1,3,10), repo.getPositions(videoId));
    }

    @Test
    void getPositionsNullReturnsEmpty() {
        when(list.lrange(any(), eq(0L), eq(-1L))).thenReturn(null);
        assertEquals(List.of(), repo.getPositions(videoId));
    }

    @Test
    void isZipDoneTrueWhenValueIs1() {
        when(value.get(any())).thenReturn("1");
        assertTrue(repo.isZipDone(videoId));
    }

    @Test
    void isZipDoneFalseOtherwise() {
        when(value.get(any())).thenReturn(null);
        assertFalse(repo.isZipDone(videoId));
        when(value.get(any())).thenReturn("0");
        assertFalse(repo.isZipDone(videoId));
    }

    @Test
    void markZipDoneSetsValue() {
        repo.markZipDone(videoId);
        verify(value).set("video:" + videoId + ":zip_done", "1");
    }

    @Test
    void tryAcquireZipLockSetsExpireOnSuccess() {
        when(value.setnx(any(), any())).thenReturn(true);
        boolean ok = repo.tryAcquireZipLock(videoId, 30);
        assertTrue(ok);
        verify(keys).expire("video:" + videoId + ":zip_lock", 30);
    }

    @Test
    void tryAcquireZipLockReturnsFalseWithoutExpireWhenNotAcquired() {
        when(value.setnx(any(), any())).thenReturn(false);
        boolean ok = repo.tryAcquireZipLock(videoId, 30);
        assertFalse(ok);
        verify(keys, never()).expire(any(), anyLong());
    }
}
