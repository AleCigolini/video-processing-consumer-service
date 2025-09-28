package br.com.video.processing.infrastructure.redis;

import br.com.video.processing.common.interfaces.ChunkProgressRepository;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RedisChunkProgressRepository implements ChunkProgressRepository {

    private final ListCommands<String, String> list;
    private final SetCommands<String, String> set;
    private final ValueCommands<String, String> value;
    private final KeyCommands<String> keys;

    @ConfigProperty(name = "redis.chunk.ttl-seconds")
    long ttlSeconds = 600;

    @Inject
    public RedisChunkProgressRepository(RedisDataSource ds) {
        this.list = ds.list(String.class);
        this.set = ds.set(String.class);
        this.value = ds.value(String.class);
        this.keys = ds.key();
    }

    private String positionsListKey(long videoId) { return "video:" + videoId + ":positions"; }
    private String positionsSetKey(long videoId)  { return "video:" + videoId + ":positions:uniq"; }
    private String zipDoneKey(long videoId)      { return "video:" + videoId + ":zip_done"; }
    private String zipLockKey(long videoId)      { return "video:" + videoId + ":zip_lock"; }

    @Override
    public long addPosition(long videoId, int position) {
        String pos = Integer.toString(position);
        String setKey = positionsSetKey(videoId);
        String listKey = positionsListKey(videoId);
        long added = set.sadd(setKey, pos);
        if (added == 1) {
            list.rpush(listKey, pos);
        }
        keys.expire(setKey, ttlSeconds);
        keys.expire(listKey, ttlSeconds);
        return set.scard(setKey);
    }

    @Override
    public long getCount(long videoId) {
        return set.scard(positionsSetKey(videoId));
    }

    @Override
    public List<Integer> getPositions(long videoId) {
        List<String> vals = list.lrange(positionsListKey(videoId), 0, -1);
        if (vals == null) return List.of();
        return vals.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    public boolean isZipDone(long videoId) {
        String v = value.get(zipDoneKey(videoId));
        return v != null && v.equals("1");
    }

    @Override
    public void markZipDone(long videoId) {
        String key = zipDoneKey(videoId);
        value.set(key, "1");
        keys.expire(key, ttlSeconds);
    }

    @Override
    public boolean tryAcquireZipLock(long videoId, long ttlSeconds) {
        boolean acquired = value.setnx(zipLockKey(videoId), "1");
        if (acquired) {
            keys.expire(zipLockKey(videoId), ttlSeconds);
        }
        return acquired;
    }
}
