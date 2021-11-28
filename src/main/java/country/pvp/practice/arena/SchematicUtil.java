package country.pvp.practice.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.SchematicFormat;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@UtilityClass
public class SchematicUtil {

    @SneakyThrows
    @SuppressWarnings("deprecation")
    public static boolean pasteSchematic(File file, @NotNull Location location) {
        SchematicFormat format = SchematicFormat.MCEDIT;
        CuboidClipboard clipboard = format.load(file);
        Vector pastePos = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        EditSession editSession = new EditSession(new BukkitWorld(location.getWorld()), 999999);
        clipboard.place(editSession, pastePos, true);
        return true;
    }


}

