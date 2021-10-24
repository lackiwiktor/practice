package country.pvp.practice.kit;

import country.pvp.practice.data.SerializableObject;
import country.pvp.practice.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
public class Kit implements SerializableObject {

    private ItemStack[] inventory;
    private ItemStack[] armor;

    @Override
    public Document getDocument() {
        Document document = new Document();
        document.put(
                "inventory",
                Arrays.stream(inventory).map(ItemStackAdapter::toJson).collect(Collectors.toList()));
        document.put(
                "armor", Arrays.stream(armor).map(ItemStackAdapter::toJson).collect(Collectors.toList()));
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        inventory = document.getList("inventory", String.class).stream().map(ItemStackAdapter::fromJson).toArray(ItemStack[]::new);
        armor = document.getList("armor", String.class).stream().map(ItemStackAdapter::fromJson).toArray(ItemStack[]::new);
    }
}
