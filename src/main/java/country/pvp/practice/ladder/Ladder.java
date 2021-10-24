package country.pvp.practice.ladder;

import country.pvp.practice.data.DataObject;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.kit.Kit;
import country.pvp.practice.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

@Data
public class Ladder implements DataObject {

    private final String name;
    private String displayName;
    private ItemStack icon;
    private Kit kit = new Kit();
    private boolean ranked;

    @Override
    public Document getDocument() {
        Document document = new Document("_id", getId());
        document.put("displayName", displayName);
        document.put("icon", ItemStackAdapter.toJson(icon));
        document.put("kit", kit.getDocument());
        return document;
    }

    @Override
    public String getCollection() {
        return "ladders";
    }

    @Override
    public String getId() {
        return name;
    }

    public boolean isSetup() {
        return displayName != null && icon != null;
    }

    public ItemStack getIcon() {
        return new ItemBuilder(icon).name(displayName).build();
    }

    public void setInventory(ItemStack[] inventory) {
        this.kit.setInventory(inventory);
    }

    public void setArmor(ItemStack[] armor) {
        this.kit.setArmor(armor);
    }

    @Override
    public void applyDocument(Document document) {
        displayName = document.getString("displayName");
        icon = ItemStackAdapter.fromJson(document.getString("icon"));
        (kit = new Kit()).applyDocument(document.get("kit", Document.class));
    }
}
