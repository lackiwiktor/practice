package country.pvp.practice.arena;

import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.Objects;

@Data
public class ChunkWrapper {

    private final World world;
    private final int x, z;

    public Chunk getChunk() {
        return world.getChunkAt(x, z);
    }

    public static ChunkWrapper of(Chunk chunk) {
        return new ChunkWrapper(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkWrapper that = (ChunkWrapper) o;
        return x == that.x && z == that.z && Objects.equals(world, that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z);
    }
}
