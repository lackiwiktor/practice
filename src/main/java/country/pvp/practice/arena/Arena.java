package country.pvp.practice.arena;

import country.pvp.practice.data.DataObject;
import country.pvp.practice.serialization.ItemStackAdapter;
import country.pvp.practice.serialization.LocationAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@Data
public class Arena implements DataObject {

    private final String name;
    private String displayName;
    private String schematic;
    private ItemStack icon;
    private Location spawnLocation1;
    private Location spawnLocation2;
    private Location spectatorLocation;
    private Location center;

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
        document.put("spawnLocation1", LocationAdapter.toJson(spawnLocation1));
        document.put("spawnLocation2", LocationAdapter.toJson(spawnLocation2));
        document.put("spectatorLocation", LocationAdapter.toJson(spectatorLocation));
        document.put("center", LocationAdapter.toJson(center));
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        displayName = document.getString("displayName");
        schematic = document.getString("schematic");
        icon = ItemStackAdapter.fromJson(document.getString("icon"));
        spawnLocation1 = LocationAdapter.fromJson(document.getString("spawnLocation1"));
        spawnLocation2 = LocationAdapter.fromJson(document.getString("spawnLocation2"));
        spectatorLocation = LocationAdapter.fromJson(document.getString("spectatorLocation"));
        center = LocationAdapter.fromJson(document.getString("center"));
    }

    public boolean isSetup() {
        return spawnLocation1 != null && spawnLocation2 != null && spectatorLocation != null && center != null;
    }

}
