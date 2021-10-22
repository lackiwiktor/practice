package country.pvp.practice.kit;

import com.google.common.collect.Maps;
import country.pvp.practice.data.DataObject;
import country.pvp.practice.data.DataRepository;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.serialization.ItemStackAdapter;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Kit implements DataObject {

  private static final Map<String, Kit> KITS = Maps.newHashMap();

  private final String name;
  private String displayName;
  private ItemStack icon;
  private ItemStack[] inventory;
  private ItemStack[] armor;
  private boolean ranked;

  public static Kit get(String name) {
    return KITS.get(name.toUpperCase(Locale.ROOT));
  }

  public static void remove(String name) {
    KITS.remove(name.toUpperCase(Locale.ROOT));
  }

  public static Set<Kit> kits() {
    return Collections.unmodifiableSet(new HashSet<>(KITS.values()));
  }

  public static void load() {
    DataRepository.collection("kits")
        .find()
        .forEach(
            document -> {
              Kit kit = new Kit(document.getString("_id"));
              kit.load(document);
              kit.cache();
            });
  }

  @Override
  public Document toDocument() {
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
  public void load(Document document) {
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

  @Override
  public String getCollection() {
    return "kits";
  }

  @Override
  public String getId() {
    return name;
  }

  public void cache() {
    KITS.put(name.toUpperCase(Locale.ROOT), this);
  }

  public boolean isSetup() {
    return displayName != null && icon != null && inventory != null && armor != null;
  }

  public ItemStack getIcon() {
    return new ItemBuilder(icon).name(displayName).build();
  }
}
