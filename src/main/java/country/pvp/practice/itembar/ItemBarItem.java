package country.pvp.practice.itembar;

import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class ItemBarItem {
  private final ItemStack item;
  private final BiConsumer<PracticePlayer, BarInteract> click;

  public void click(PracticePlayer player, BarInteract interact) {
    click.accept(player, interact);
  }

  public ItemStack item() {
    return item.clone();
  }

  public boolean isSimilar(ItemStack item) {
    return this.item.isSimilar(item);
  }
}
