package country.pvp.practice.arena;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.io.File;

@UtilityClass
public class SchematicUtil {

    @SneakyThrows
    @SuppressWarnings("deprecation")
    public static boolean pasteSchematic(File file, Location location) {
       /*
        SchematicFormat format = SchematicFormat.MCEDIT;
        CuboidClipboard clipboard = format.load(file);
        Vector pastePos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        EditSession editSession = new EditSession(new BukkitWorld(location.getWorld()), 999999);
        clipboard.place(editSession, pastePos, true);
        return true;
        */
        return false;
    }


}

