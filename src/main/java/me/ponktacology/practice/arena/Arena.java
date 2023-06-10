package me.ponktacology.practice.arena;

import me.ponktacology.practice.util.data.DataObject;
import me.ponktacology.practice.util.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

@Data
public class Arena implements DataObject {

  private final String name;
  private final ArenaType arenaType;
  private String displayName;
  private ItemStack icon;
  private int index;

  private boolean occupied;

  public Arena(String name, ArenaType arenaType) {
    this.name = name;
    this.displayName = name;
    this.arenaType = arenaType;
  }

  public boolean isSetup() {
    return icon != null && displayName != null;
  }

  @Override
  public String getId() {
    return name;
  }

  @Override
  public Document getDocument() {
    Document document = new Document("_id", getId());
    document.put("displayName", displayName);
    document.put("icon", ItemStackAdapter.toJson(icon));
    document.put("type", arenaType.toString());
    return document;
  }

  @Override
  public void applyDocument(Document document) {
    displayName = document.getString("displayName");
    icon = ItemStackAdapter.fromJson(document.getString("icon"));
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
