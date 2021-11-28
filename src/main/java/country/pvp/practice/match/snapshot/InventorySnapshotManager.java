package country.pvp.practice.match.snapshot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InventorySnapshotManager {

    private final Cache<UUID, InventorySnapshot> snapshots = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();

    public void add(InventorySnapshot snapshot) {
        snapshots.put(snapshot.getId(), snapshot);
    }

    public Optional<InventorySnapshot> get(UUID id) {
        return Optional.ofNullable(snapshots.getIfPresent(id));
    }
}
