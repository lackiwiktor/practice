package country.pvp.practice.util;

import com.sk89q.worldedit.Vector;
import lombok.Data;
import org.bukkit.Location;

@Data
public class Region {

    private final int minX, maxX, minY, maxY, minZ, maxZ;

    public static Region from(Location first, Location second) {
        Region region = new Region(
                Math.min(first.getBlockX(), second.getBlockX()),
                Math.max(first.getBlockX(), second.getBlockX()),
                Math.min(first.getBlockY(), second.getBlockY()),
                Math.max(first.getBlockY(), second.getBlockY()),
                Math.min(first.getBlockZ(), second.getBlockZ()),
                Math.max(first.getBlockZ(), second.getBlockZ())
        );

        return region;
    }

    public static Region from(Selection selection) {
        Location first = selection.getFirst();
        Location second = selection.getSecond();

        return from(first, second);
    }

    public Vector getMin() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMax() {
        return new Vector(maxX, maxY, maxZ);
    }

    public boolean isIn(Location location) {
        return location.getX() <= maxX && location.getX() >= minX &&
                location.getY() <= maxY && location.getY() >= minY &&
                location.getZ() <= maxZ && location.getZ() >= minZ;
    }
}
