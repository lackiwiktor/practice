package country.pvp.practice.itembar;

import country.pvp.practice.player.PlayerSession;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Data
public class ItemBar {

    private final ItemBarItem[] items;

    public ItemBar(ItemBarItem... items) {
        this.items = items;
    }

    public void apply( PlayerSession player) {
        player.setBar(bar());
    }

    public ItemStack [] bar() {
        return Arrays.stream(items).map(it -> it == null ? new ItemStack(Material.AIR) : it.getItem()).toArray(ItemStack[]::new);
    }
}
