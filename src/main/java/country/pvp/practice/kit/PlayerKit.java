package country.pvp.practice.kit;

import country.pvp.practice.itembar.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class PlayerKit extends Kit {

    private String name;

    @Override
    public Document getDocument() {
        Document document = super.getDocument();
        document.put("name", name);
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        super.applyDocument(document);
        name = document.getString("name");
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.ENCHANTED_BOOK)
                .name(name)
                .build();
    }
}
