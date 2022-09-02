package me.ponktacology.practice.match;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.snapshot.InventorySnapshotService;
import me.ponktacology.practice.match.statistics.PlayerMatchStatistics;
import me.ponktacology.practice.player.PracticePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class InventorySnapshotTracker {

  private final Map<PracticePlayer, InventorySnapshot> snapshots = Maps.newHashMap();

  public InventorySnapshot createInventorySnapshot(
      PracticePlayer session, PlayerMatchStatistics statistics) {
    InventorySnapshot snapshot = InventorySnapshot.create(session, statistics);
    snapshots.put(session, snapshot);
    return snapshot;
  }

  public void commit() {
    snapshots.values().forEach(it -> it.setCreatedAt(System.currentTimeMillis()));
    Practice.getService(InventorySnapshotService.class).addAll(snapshots.values());
  }

  public @Nullable InventorySnapshot get(PracticePlayer player) {
    return snapshots.get(player);
  }

  public Map<PracticePlayer, InventorySnapshot> get() {
    return Collections.unmodifiableMap(snapshots);
  }

  public void clear() {
    snapshots.clear();
  }
}
