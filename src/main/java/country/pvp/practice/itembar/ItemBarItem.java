package country.pvp.practice.itembar;

import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class ItemBarItem {

  private final @NotNull ItemStack item;
  private final @NotNull BiConsumer<PracticePlayer, BarInteract> click;

  public void click(PracticePlayer player, BarInteract interact) {
    click.accept(player, interact);
  }

  public ItemStack getItem() {
    return item.clone();
  }

  public boolean isSimilar(ItemStack item) {
    return this.item.isSimilar(item);
  }
}
