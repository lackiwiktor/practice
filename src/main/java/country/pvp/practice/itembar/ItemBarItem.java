package country.pvp.practice.itembar;

import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class ItemBarItem {

  private final ItemStack item;
  private final Consumer<PlayerSession> click;

  void click(PlayerSession player) {
    click.accept(player);
  }

  ItemStack getItem() {
    return item.clone();
  }

  boolean isSimilar(ItemStack item) {
    return this.item.isSimilar(item);
  }
}
