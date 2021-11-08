package country.pvp.practice.itembar;

import com.google.common.collect.Maps;
import country.pvp.practice.Practice;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.Queue;
import country.pvp.practice.queue.QueueData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemBarManager {

    private final Map<ItemBarType, ItemBar> itemBars = Maps.newHashMap();

    public ItemBarManager() {
        setupItemBars();
    }

    private void setupItemBars() {
        add(ItemBarType.LOBBY, new ItemBar(
                new ItemBarItem(new ItemBuilder(Material.IRON_SWORD).name("Unranked").unbreakable().build(),
                        ((player, interact) -> {
                            Practice.getQueueMenuProvider().provide(false, player).openMenu(player.getPlayer());
                        }))));
        add(ItemBarType.QUEUE, new ItemBar(
                new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("Leave Queue").unbreakable().build(),
                        ((player, interact) -> {
                            QueueData queueData = player.getStateData(PlayerState.QUEUING);
                            Queue queue = queueData.getQueue();
                            queue.removePlayer(player, true);
                        }))));
    }

    public boolean click(PracticePlayer player, ItemStack item, BarInteract interact) {
        for (ItemBar bar : itemBars.values()) {
            for (ItemBarItem itemBarItem : bar.getItems()) {
                if (itemBarItem.isSimilar(item)) {
                    itemBarItem.click(player, interact);
                    return true;
                }
            }
        }

        return false;
    }

    public void add(ItemBarType type, ItemBar itemBar) {
        itemBars.put(type, itemBar);
    }

    public ItemBar get(ItemBarType type) {
        return itemBars.get(type);
    }

    public void apply(ItemBarType type, PracticePlayer player) {
        get(type).apply(player);
    }
}
