package country.pvp.practice.ladder;

import country.pvp.practice.util.data.DataObject;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.kit.Kit;
import country.pvp.practice.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class Ladder implements DataObject {

    private final String name;
    private String displayName;
    private ItemStack icon;
    private Kit kit = new Kit();
    private ItemStack[] editorItems;
    private boolean ranked;
    private boolean build;

    @Override
    public Document getDocument() {
        Document document = new Document("_id", getId());
        document.put("displayName", displayName);
        document.put("icon", ItemStackAdapter.toJson(icon));
        document.put("kit", kit.getDocument());
        document.put("ranked", ranked);
        document.put("build", build);
        document.put("editorItems", Arrays.stream(editorItems).map(ItemStackAdapter::toJson).collect(Collectors.toList()));
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        displayName = document.getString("displayName");
        icon = ItemStackAdapter.fromJson(document.getString("icon"));
        kit.applyDocument(document.get("kit", Document.class));
        ranked = document.getBoolean("ranked");
        build = document.getBoolean("build");
        editorItems = (ItemStack[]) document.get("editorItems", List.class).stream().map(it -> ItemStackAdapter.fromJson((String) it)).toArray(ItemStack[]::new);
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

    public @Nullable ItemStack getIcon() {
        if (!isSetup()) return null;

        return new ItemBuilder(icon.clone()).name(displayName).build();
    }

    public void setInventory(ItemStack... inventory) {
        kit.setInventory(inventory);
    }

    public void setArmor(ItemStack... armor) {
        kit.setArmor(armor);
    }

    public ItemStack[] getEditorItems() {
        return editorItems == null ? new ItemStack[0] : Arrays.stream(editorItems).map(it -> it == null ? new ItemStack(Material.AIR) : it.clone()).toArray(ItemStack[]::new);
    }

    public void setEditorItems(ItemStack... items) {
        editorItems = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ladder ladder = (Ladder) o;
        return Objects.equals(name, ladder.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
