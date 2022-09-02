package me.ponktacology.practice.arena.match;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.ponktacology.practice.arena.Arena;
import me.ponktacology.practice.arena.ArenaType;
import me.ponktacology.practice.util.*;
import me.ponktacology.practice.util.serialization.LocationAdapter;
import net.frozenorb.chunksnapshot.ChunkSnapshot;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.craftbukkit.v1_8_R3.util.LongHash;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class MatchArenaCopy extends Arena implements Restorable {

  private final Arena parent;
  private final UUID id;
  private final Map<Long, ChunkSnapshot> chunkSnapshots = Maps.newHashMap();

  private Location spawnLocation1;
  private Location spawnLocation2;
  private Region region;

  public MatchArenaCopy(Arena parent, Region region) {
    this(UUID.randomUUID(), parent);
    this.region = region;
  }

  public MatchArenaCopy(UUID uuid, Arena parent) {
    super(parent.getName(), ArenaType.MATCH);
    this.id = uuid;
    this.parent = parent;
  }

  public void scanLocations() {
    forEachBlock(
        block -> {
          Material type = block.getType();

          if (type != Material.SKULL) {
            return;
          }

          Skull skull = (Skull) block.getState();
          Block below = block.getRelative(BlockFace.DOWN);

          Location skullLocation = block.getLocation();
          skullLocation.setYaw(AngleUtil.faceToYaw(skull.getRotation()) + 90);

          if (spawnLocation1 == null) {
            spawnLocation1 = skullLocation;
          } else {
            spawnLocation2 = skullLocation;
          }

          block.setType(Material.AIR);

          if (below.getType() == Material.FENCE) {
            below.setType(Material.AIR);
          }
        });

    Preconditions.checkNotNull(spawnLocation1, "Team 1 spawn (player skull) cannot be null.");
    Preconditions.checkNotNull(spawnLocation2, "Team 2 spawn (player skull) cannot be null.");

    takeSnapshot();
  }

  private void forEachBlock(Consumer<Block> callback) {
    World world = Bukkit.getWorld("arenas");

    Location start = WorldEditUtils.vectorToLocation(world, region.getMin());
    Location end = WorldEditUtils.vectorToLocation(world, region.getMax());

    for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
      for (int y = start.getBlockY(); y < end.getBlockY(); y++) {
        for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
          callback.accept(world.getBlockAt(x, y, z));
        }
      }
    }
  }

  private void forEachChunk(Consumer<Chunk> callback) {
    World world = Bukkit.getWorld("arenas");

    for (int x = region.getMinX() >> 4; x <= region.getMaxX() >> 4; x++) {
      for (int z = region.getMinZ() >> 4; z <= region.getMaxZ() >> 4; z++) {
        callback.accept(world.getChunkAt(x, z));
      }
    }
  }

  public Location getSpawnLocation1() {
    return spawnLocation1.clone().add(0.5, 1.5, 0.5);
  }

  public Location getSpawnLocation2() {
    return spawnLocation2.clone().add(0.5, 1.5, 0.5);
  }

  public Location getCenter() {
    return WorldEditUtils.vectorToLocation(
        spawnLocation1.getWorld(),
        spawnLocation1.toVector().getMidpoint(spawnLocation2.toVector()));
  }

  public boolean isIn(Location location) {
    return region.isIn(location);
  }

  @Override
  public String getName() {
    return parent.getName();
  }

  @Override
  public String getDisplayName() {
    return parent.getDisplayName();
  }

  @Override
  public ItemStack getIcon() {
    return parent.getIcon();
  }

  @Override
  public String getId() {
    return id.toString();
  }

  @Override
  public Document getDocument() {
    Document document = new Document("_id", getId());

    document.put("spawnLocation1", LocationAdapter.toJson(spawnLocation1));
    document.put("spawnLocation2", LocationAdapter.toJson(spawnLocation2));
    document.put("region", RegionAdapter.toJson(region));

    return document;
  }

  @Override
  public void applyDocument(Document document) {
    spawnLocation1 = LocationAdapter.fromJson(document.getString("spawnLocation1"));
    spawnLocation2 = LocationAdapter.fromJson(document.getString("spawnLocation2"));
    region = RegionAdapter.fromJson(document.getString("region"));
  }

  // Taken from PotPVP-SI
  public void takeSnapshot() {
    synchronized (chunkSnapshots) {
      forEachChunk(
          chunk ->
              chunkSnapshots.put(
                  LongHash.toLong(chunk.getX(), chunk.getZ()), chunk.takeSnapshot()));
    }
  }

  // Taken from PotPVP-SI
  @Override
  public void restore() {
    synchronized (chunkSnapshots) {
      long start = System.currentTimeMillis();
      Logger.debug("Restoring arena %s id: %s", getName(), id.toString());
      World world = Bukkit.getWorld("arenas");
      chunkSnapshots.forEach(
          (key, value) ->
              world.getChunkAt(LongHash.msw(key), LongHash.lsw(key)).restoreSnapshot(value));
      Logger.debug("Restored arena in %d ms.", TimeUtil.elapsed(start));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MatchArenaCopy arenaCopy = (MatchArenaCopy) o;
    return Objects.equal(id, arenaCopy.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), id);
  }
}
