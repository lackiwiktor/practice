package country.pvp.practice.arena;

import country.pvp.practice.PracticePlugin;
import country.pvp.practice.util.Region;
import country.pvp.practice.util.RegionAdapter;
import country.pvp.practice.util.data.DataObject;
import country.pvp.practice.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

@Data
public class Arena implements DataObject {

    public static final File WORLD_EDIT_SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(PracticePlugin.class).getDataFolder(), "schematics");

    private final String name;
    private String displayName;
    private String schematic;
    private ItemStack icon;
    private int gridIndex;
    private boolean occupied;
    Region region;

    @Override
    public String getCollection() {
        return "arenas";
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public Document getDocument() {
        Document document = new Document("_id", getId());
        document.put("displayName", displayName);
        document.put("schematic", schematic);
        document.put("icon", ItemStackAdapter.toJson(icon));
        document.put("gridIndex", gridIndex);
        document.put("region", RegionAdapter.toJson(region));

        return document;
    }

    @Override
    public void applyDocument(Document document) {
        displayName = document.getString("displayName");
        schematic = document.getString("schematic");
        icon = ItemStackAdapter.fromJson(document.getString("icon"));
        region = RegionAdapter.fromJson(document.getString("region"));
        gridIndex = document.getInteger("gridIndex");
    }

    public boolean isIn(Location location) {
        return region.isIn(location);
    }

    public boolean isSetup() {
        return icon != null && displayName != null && region != null;
    }

    public @Nullable File getSchematic() {
        return Paths.get(WORLD_EDIT_SCHEMATICS_FOLDER.getAbsolutePath(), name.concat(".schematic")).toFile();
    }

    public synchronized void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return Objects.equals(name, arena.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
