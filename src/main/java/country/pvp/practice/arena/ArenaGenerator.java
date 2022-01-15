package country.pvp.practice.arena;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import country.pvp.practice.util.Region;
import country.pvp.practice.util.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.Set;

public class ArenaGenerator {

    public static final Vector STARTING_POINT = new Vector(1_000, 60, 1_000);

    public static Set<DuplicatedArena> generate(Arena parent, int amount) {
        Set<DuplicatedArena> copies = Sets.newHashSet();

        System.out.println("Generating arenas, this may take some time...");
        for (int i = 0; i < amount; i++) {
            DuplicatedArena copy = createCopy(parent, STARTING_POINT.getBlockX() + (parent.getGridIndex() * 100), STARTING_POINT.getBlockZ() + (i * 250));
            copies.add(copy);
            System.out.println("Generating arenas " + ((copies.size() / (double) amount) * 100) + "%");
        }

        System.out.println("Scanning for spawn locations...");
        for (DuplicatedArena arena : copies) {
            arena.scanLocations();
        }

        return copies;
    }

    public static DuplicatedArena createCopy(Arena parent, int xStart, int zStart) {
        Vector pasteAt = new Vector(xStart, STARTING_POINT.getY(), zStart);
        CuboidClipboard clipboard;
        World world = Bukkit.getWorld("arenas");

        File schematic = parent.getSchematic();
        try {
            clipboard = WorldEditUtils.paste(schematic, world, pasteAt);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Location lowerCorner = WorldEditUtils.vectorToLocation(world, pasteAt);
        Location upperCorner = WorldEditUtils.vectorToLocation(world, pasteAt.add(clipboard.getSize()));

        try {
            System.out.println("Waiting :)");
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Created new copy on " + xStart + " 60 " + zStart);
        return new DuplicatedArena(parent, Region.from(lowerCorner, upperCorner));
    }
}
