package country.pvp.practice.util;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

@UtilityClass
public final class WorldEditUtils {

    public static CuboidClipboard paste(File file, World world, Vector pasteAt) throws Exception {
        CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(file);
        EditSession editSession = getEditSession(world.getName());

        clipboard.setOffset(new Vector(0, 0, 0));
        clipboard.paste(editSession, pasteAt, true);

        return clipboard;
    }

    public static void save(File file, World world, Region region) throws DataException, IOException {
        if (!file.exists()) {
            file.getParentFile().mkdir();
        }

        file.createNewFile();

        Vector min = region.getMin();
        Vector max = region.getMax();

        EditSession editSession = getEditSession(world.getName());
        editSession.enableQueue();
        CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
        clipboard.copy(editSession);
        SchematicFormat.MCEDIT.save(clipboard, file);
        editSession.flushQueue();
    }


    public static org.bukkit.Location vectorToLocation(World world, Vector vector) {
        return new org.bukkit.Location(
                world,
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ()
        );
    }

    public static EditSession getEditSession(String worldName) {
        BukkitWorld world = new BukkitWorld(Bukkit.getWorld(worldName));
        EditSessionFactory esFactory = WorldEdit.getInstance().getEditSessionFactory();
        return esFactory.getEditSession(world, Integer.MAX_VALUE);
    }

}
