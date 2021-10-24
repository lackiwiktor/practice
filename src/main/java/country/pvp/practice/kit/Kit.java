package country.pvp.practice.kit;

import country.pvp.practice.data.DataObject;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class Kit implements DataObject {

    private final String name;
    private String displayName;
    private ItemStack icon;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private boolean ranked;

    @Override
    public Document get() {
        Document document = new Document("_id", getId());
        document.put("displayName", displayName);
        document.put("icon", ItemStackAdapter.toJson(icon));
        document.put(
                "inventory",
                Arrays.stream(inventory).map(ItemStackAdapter::toJson).collect(Collectors.toList()));
        document.put(
                "armor", Arrays.stream(armor).map(ItemStackAdapter::toJson).collect(Collectors.toList()));

        return document;
    }

    @Override
    public String getCollection() {
        return "kits";
    }

    @Override
    public String getId() {
        return name;
    }

    public boolean isSetup() {
        return displayName != null && icon != null && inventory != null && armor != null;
    }

    public ItemStack getIcon() {
        return new ItemBuilder(icon).name(displayName).build();
    }

    @Override
    public void apply(Document document) {
        displayName = document.getString("displayName");
        icon = ItemStackAdapter.fromJson(document.getString("icon"));
        inventory =
                document.getList("inventory", String.class).stream()
                        .map(ItemStackAdapter::fromJson)
                        .toArray(ItemStack[]::new);
        armor =
                document.getList("armor", String.class).stream()
                        .map(ItemStackAdapter::fromJson)
                        .toArray(ItemStack[]::new);
    }
}
