package me.ponktacology.practice.ladder;

import me.ponktacology.practice.util.ItemBuilder;
import me.ponktacology.practice.kit.Kit;
import me.ponktacology.practice.util.data.DataObject;
import me.ponktacology.practice.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class Ladder implements DataObject {

    private final String name;

    private String displayName;
    private @Nullable ItemStack icon;
    private List<ItemStack> editorItems = new ArrayList<>();
    private Kit kit = new Kit();
    private boolean ranked;
    private int index;
    private boolean allowBuild = false;
    private boolean allowHealthRegeneration = true;
    private boolean hungerDecay = true;
    private boolean showHP = false;

    public Ladder(String name) {
        this.name = name;
        this.displayName = name;
    }

    @Override
    public Document getDocument() {
        Document document = new Document("_id", getId());
        document.put("displayName", displayName);
        document.put("icon", ItemStackAdapter.toJson(icon));
        document.put("kit", kit.getDocument());
        document.put("ranked", ranked);
        document.put("allowBuild", allowBuild);
        document.put("allowHealthRegeneration", allowHealthRegeneration);
        document.put("hungerDecay", hungerDecay);
        document.put("showHP", showHP);
        document.put("index", index);
        document.put("editorItems", editorItems.stream()
                .map(ItemStackAdapter::toJson)
                .collect(Collectors.toList()));
        return document;
    }

    @Override
    public void applyDocument(Document document) {
        displayName = document.getString("displayName");
        icon = ItemStackAdapter.fromJson(document.getString("icon"));
        kit.applyDocument(document.get("kit", Document.class));
        ranked = document.getBoolean("ranked");
        index = document.getInteger("index");
        allowBuild = document.getBoolean("allowBuild", allowBuild);
        allowHealthRegeneration = document.getBoolean("allowHealthRegeneration", allowHealthRegeneration);
        hungerDecay = document.getBoolean("hungerDecay", hungerDecay);
        showHP = document.getBoolean("showHP", showHP);
        editorItems = (List<ItemStack>) document.get("editorItems", List.class)
                .stream()
                .map(it -> ItemStackAdapter.fromJson((String) it))
                .collect(Collectors.toList());
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

        return new ItemBuilder(icon.clone())
                .hideAll()
                .name(displayName)
                .build();
    }

    public void setInventory(ItemStack... inventory) {
        kit.setInventory(inventory);
    }

    public void setArmor(ItemStack... armor) {
        kit.setArmor(armor);
    }

    public List<ItemStack> getEditorItems() {
        return editorItems;
    }

    public boolean isSumo() {
        return SpecialLadders.isSpecial(this, SpecialLadders.SUMO);
    }

    public boolean isBoxing() {
        return SpecialLadders.isSpecial(this, SpecialLadders.BOXING);
    }

    public boolean isBridge() {
        return SpecialLadders.isSpecial(this, SpecialLadders.BRIDGE);
    }

    public boolean isEggShot() {
        return SpecialLadders.isSpecial(this, SpecialLadders.EGGS_HOT);
    }

    public boolean isCombo() {
        return SpecialLadders.isSpecial(this, SpecialLadders.COMBO);
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
