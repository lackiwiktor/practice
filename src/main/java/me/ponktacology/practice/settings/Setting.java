package me.ponktacology.practice.settings;

import lombok.Data;
import lombok.Getter;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.ItemBuilder;
import me.ponktacology.practice.util.data.SerializableObject;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Setting<V> implements SerializableObject {

  private final String name;
  private final LinkedList<Option<V>> options = new LinkedList<>();
  private final String description;
  private final Material material;
  private int currentOption = 0;

  public Setting(
          String name,
          String description,
          Material material,
          List<Option<V>> options) {
    this.name = name;
    this.description = description;
    this.material = material;
    this.options.addAll(options);
    for (Option<V> option : options) {
      this.options.addLast(option);
    }
  }

  public void toggle(PracticePlayer player) {
    currentOption = ++currentOption % options.size();
    onToggle(player, options.get(currentOption));
  }

  public Option<V> getCurrentOption() {
    return options.get(currentOption);
  }

  public ItemStack getIcon() {
    ItemBuilder builder =
        new ItemBuilder(material).name(ChatColor.GOLD + name).lore(ChatColor.WHITE + description);
    builder.lore("");
    for (int i = 0; i < options.size(); i++) {
      Option<V> option = options.get(i);
      builder.lore((currentOption == i ? ChatColor.YELLOW : ChatColor.GRAY) + option.name);
    }
    return builder.build();
  }

  public void onToggle(PracticePlayer player, Option<V> currentOption) {}

  @Override
  public Document getDocument() {
    return new Document("option", currentOption);
  }

  @Override
  public void applyDocument(Document document) {
    currentOption = document.getInteger("option", currentOption);
  }

  @Data
  public static class Option<V> {
    private static final Option<Boolean> ENABLED = new Option<>("Enabled", true);
    private static final Option<Boolean> DISABLED = new Option<>("Disabled", false);

    private final String name;
    private final V value;
  }
}
