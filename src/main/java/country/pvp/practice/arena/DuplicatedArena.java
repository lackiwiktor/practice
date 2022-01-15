package country.pvp.practice.arena;

import com.google.common.base.Preconditions;
import country.pvp.practice.util.AngleUtil;
import country.pvp.practice.util.Region;
import country.pvp.practice.util.RegionAdapter;
import country.pvp.practice.util.WorldEditUtils;
import country.pvp.practice.util.data.Callback;
import country.pvp.practice.util.serialization.LocationAdapter;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Getter
public class DuplicatedArena extends Arena {

    private final Arena parent;
    private final UUID id;


    public DuplicatedArena(Arena parent, Region region) {
        this(UUID.randomUUID(), parent);
        this.region = region;
    }

    public DuplicatedArena(UUID uuid, Arena parent) {
        super(parent.getName());
        this.id = uuid;
        this.parent = parent;
    }

    void scanLocations() {
        forEachBlock(block -> {
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
        Document document = new Document("parent", parent.getName());

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


    @Override
    public boolean isSetup() {
        return parent.isSetup();
    }

    @Override
    public @Nullable File getSchematic() {
        return parent.getSchematic();
    }

    @Override
    public Location getCenter() {
        return parent.getCenter();
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
