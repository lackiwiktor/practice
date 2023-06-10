package me.ponktacology.practice.match.snapshot;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.match.snapshot.command.SnapshotCommands;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class InventorySnapshotService extends Service {

  @Override
  public void configure() {
    addCommand(new SnapshotCommands(this));

    registerTask(this::invalidate, 1L, TimeUnit.SECONDS, true);
  }

  private final Map<UUID, InventorySnapshot> snapshots = Maps.newConcurrentMap();

  public void add(InventorySnapshot snapshot) {
    snapshots.put(snapshot.getId(), snapshot);
  }

  public Optional<InventorySnapshot> get(UUID id) {
    return Optional.ofNullable(snapshots.get(id));
  }

  private void invalidate() {
    snapshots.entrySet().removeIf(entry -> entry.getValue().hasExpired());
  }

  public void addAll(Collection<InventorySnapshot> snapshots) {
    snapshots.forEach(this::add);
  }
}
