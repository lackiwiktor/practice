package me.ponktacology.practice.arena.match;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import me.ponktacology.practice.util.Logger;
import me.ponktacology.practice.util.Region;
import me.ponktacology.practice.util.WorldEditUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchArenaGenerator {

  public static final Vector STARTING_POINT = new Vector(1_000, 60, 1_000);

  public static Set<MatchArenaCopy> generate(MatchArena parent, int amount) {
    Set<Region> regionCopies = Sets.newHashSet();
    int currentCopiesAmount = parent.getCopies().size();
    Logger.log("Generating arenas, this may take some time...");
    for (int i = currentCopiesAmount; i < currentCopiesAmount + amount; i++) {
      Region region =
          pasteCopy(
              parent,
              STARTING_POINT.getBlockX() + (parent.getGridIndex() * 1000),
              STARTING_POINT.getBlockZ() + (i * 1000));

      regionCopies.add(region);
      Logger.log("Generating arenas " + ((regionCopies.size() / (double) amount) * 100) + "%");
    }

    Set<MatchArenaCopy> matchArenaCopies =
        regionCopies.stream().map(it -> new MatchArenaCopy(parent, it)).collect(Collectors.toSet());
    Logger.log("Scanning for spawn locations...");

    for (MatchArenaCopy arena : matchArenaCopies) {
      arena.scanLocations();
    }

    return matchArenaCopies;
  }

  public static Region pasteCopy(MatchArena parent, int xStart, int zStart) {
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

    Logger.log("Created new copy on " + xStart + " 60 " + zStart);
    return Region.from(lowerCorner, upperCorner);
  }
}
