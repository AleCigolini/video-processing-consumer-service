package br.com.video.processing.infrastructure.redis;

import br.com.video.processing.common.interfaces.ChunkProgressRepository;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class RedisChunkProgressRepository implements ChunkProgressRepository {

    private final ListCommands<String, String> list;
    private final SetCommands<String, String> set;
    private final ValueCommands<String, String> value;
    private final KeyCommands<String> keys;

    @Inject
    public RedisChunkProgressRepository(RedisDataSource ds) {
        this.list = ds.list(String.class);
        this.set = ds.set(String.class);
        this.value = ds.value(String.class);
        this.keys = ds.key();
    }

    private String positionsListKey(UUID videoId) { return "video:" + videoId + ":positions"; }
    private String positionsSetKey(UUID videoId)  { return "video:" + videoId + ":positions:uniq"; }
    private String zipDoneKey(UUID videoId)      { return "video:" + videoId + ":zip_done"; }
    private String zipLockKey(UUID videoId)      { return "video:" + videoId + ":zip_lock"; }

    @Override
    public long addPosition(UUID videoId, int position) {
        String pos = Integer.toString(position);
        String setKey = positionsSetKey(videoId);
        long added = set.sadd(setKey, pos);
        if (added == 1) {
            list.rpush(positionsListKey(videoId), pos);
        }
        return set.scard(setKey);
    }

    @Override
    public long getCount(UUID videoId) {
        return set.scard(positionsSetKey(videoId));
    }

    @Override
    public List<Integer> getPositions(UUID videoId) {
        List<String> vals = list.lrange(positionsListKey(videoId), 0, -1);
        if (vals == null) return List.of();
        return vals.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    public boolean isZipDone(UUID videoId) {
        String v = value.get(zipDoneKey(videoId));
        return v != null && v.equals("1");
    }

    @Override
    public void markZipDone(UUID videoId) {
        value.set(zipDoneKey(videoId), "1");
    }

    @Override
    public boolean tryAcquireZipLock(UUID videoId, long ttlSeconds) {
        boolean acquired = value.setnx(zipLockKey(videoId), "1");
        if (acquired) {
            keys.expire(zipLockKey(videoId), ttlSeconds);
        }
        return acquired;
    }
}
