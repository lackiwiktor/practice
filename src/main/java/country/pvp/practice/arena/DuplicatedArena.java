package country.pvp.practice.arena;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import country.pvp.practice.util.AngleUtil;
import country.pvp.practice.util.Region;
import country.pvp.practice.util.RegionAdapter;
import country.pvp.practice.util.WorldEditUtils;
import country.pvp.practice.util.data.Callback;
import country.pvp.practice.util.serialization.LocationAdapter;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class DuplicatedArena extends Arena {

    private final Arena parent;
    private final UUID id;
    private final Set<Location> placedBlocks = Sets.newConcurrentHashSet();
    private final Set<ChunkWrapper> chunks = Sets.newHashSet();
    private Location spawnLocation1;
    private Location spawnLocation2;

    public DuplicatedArena(Arena parent, Region region) {
        this(UUID.randomUUID(), parent);
        this.region = region;
    }

    public DuplicatedArena(UUID uuid, Arena parent) {
        super(parent.getName());
        this.id = uuid;
        this.parent = parent;
    }

    public boolean hasBeenPlacedByPlayer(Block block) {
        return placedBlocks.contains(block.getLocation());
    }

    public void addPlacedBlock(Block block) {
        placedBlocks.add(block.getLocation());
    }

    public void removePlacedBlock(Block block) {
        placedBlocks.remove(block.getLocation());
    }

    void cacheChunks() {
        chunks.clear();
        forEachBlock(block -> {
            cacheChunk(block);
        });
    }

    private void cacheChunk(Block block) {
        World world = block.getWorld();
        int x = block.getX();
        int z = block.getZ();

        Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
        ChunkWrapper chunkWrapper = ChunkWrapper.of(chunk);
        chunks.add(chunkWrapper);
    }

    void scanLocations() {
        forEachBlock(block -> {
            cacheChunk(block);

            Material type = block.getType();

            if (type != Material.SKULL) {
                return;
            }

            Skull skull = (Skull) block.getState();
            Block below = block.getRelative(BlockFace.DOWN);

            Location skullLocation = block.getLocation().clone().add(0.5, 1.5, 0.5);
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
    }

    private void forEachBlock(Callback<Block> callback) {
        World world = Bukkit.getWorld("arenas");

        Location start = WorldEditUtils.vectorToLocation(world, region.getMin());
        Location end = WorldEditUtils.vectorToLocation(world, region.getMax());

        for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y < end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                    callback.call(world.getBlockAt(x, y, z));
                }
            }
        }
    }

    public void cleanUp() {
        removeEntities();
        cleanPlacedBlocks();
    }

    public void removeEntities() {
        Set<Chunk> coveredChunks = getChunks()
                .stream()
                .map(it -> it.getChunk())
                .collect(Collectors.toSet());

        coveredChunks.forEach(chunk -> {
            chunk.load();
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Item) {
                    entity.remove();
                }
            }
        });
    }

    public synchronized void cleanPlacedBlocks() {
        placedBlocks.forEach(it -> it.getBlock().setType(Material.AIR));
        placedBlocks.clear();
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
    public String getCollection() {
        return "duplicated_arenas";
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public Document getDocument() {
        Document document = new Document("_id", getId());

        document.put("parent", parent.getName());
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

        if (region != null) {
            cacheChunks();
        }
    }


    @Override
    public boolean isSetup() {
        return parent.isSetup();
    }

    @Override
    public @Nullable File getSchematic() {
        return parent.getSchematic();
    }


    public Location getCenter() {
        return WorldEditUtils.vectorToLocation(spawnLocation1.getWorld(), spawnLocation1.toVector().getMidpoint(spawnLocation2.toVector()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DuplicatedArena arena = (DuplicatedArena) o;
        return Objects.equals(id, arena.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
