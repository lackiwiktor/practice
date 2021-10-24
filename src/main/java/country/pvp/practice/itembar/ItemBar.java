package country.pvp.practice.itembar;

import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ItemBar {
  LOBBY(
      new ItemBarItem[] {
        new ItemBarItem(
            new ItemBuilder(Material.IRON_SWORD).name("Unranked").unbreakable().build(),
            ((player, interact) -> Messager.message(player, "Clicked!")))
      });

  private final ItemBarItem[] items;

  public static boolean click(PracticePlayer player, ItemStack item, BarInteract interact) {
    for (ItemBar bar : ItemBar.values()) {
      for (ItemBarItem itemBarItem : bar.items) {
        if (itemBarItem.isSimilar(item)) {
          itemBarItem.click(player, interact);
          return true;
        }
      }
    }

    return false;
  }

  public void apply(PracticePlayer player) {
    player.setBar(bar());
  }

  public ItemStack[] bar() {
    return Arrays.stream(items).map(ItemBarItem::getItem).toArray(ItemStack[]::new);
  }

}
