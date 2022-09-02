package me.ponktacology.practice.kit;

import me.ponktacology.practice.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@AllArgsConstructor
@Data
public class NamedKit extends Kit {

    private String name;

    public static NamedKit from(String name, Kit kit) {
        NamedKit namedKit = new NamedKit(name);

        namedKit.setArmor(kit.getArmor());
        namedKit.setInventory(kit.getInventory());

        return namedKit;
    }

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
                .name("&e".concat(name))
                .build();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedKit namedKit = (NamedKit) o;
        return Objects.equals(name, namedKit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
