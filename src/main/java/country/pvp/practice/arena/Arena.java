package country.pvp.practice.arena;

import country.pvp.practice.data.DataObject;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Location;

@Data
public class Arena implements DataObject {

    private final String name;
    private String schematic;
    private Location spawnLocation1, spawnLocation2;
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
        document.put("schematic", schematic);
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        schematic = document.getString("schematic");
    }

    public void generate() {
        //TODO: generate arena from schematic
    }
}
