package country.pvp.practice.itembar;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.MatchType;
import country.pvp.practice.queue.QueueManager;
import country.pvp.practice.queue.QueueMenuProvider;
import country.pvp.practice.team.SoloTeam;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemBarManager {

    private final Map<ItemBarType, ItemBar> itemBars = Maps.newHashMap();
    private final QueueMenuProvider queueMenuProvider;
    private final QueueManager queueManager;

    @Inject
    public ItemBarManager(QueueMenuProvider queueMenuProvider, QueueManager queueManager) {
        this.queueMenuProvider = queueMenuProvider;
        this.queueManager = queueManager;
        setupItemBars();
    }

    private void setupItemBars() {
        add(ItemBarType.LOBBY, new ItemBar(
                new ItemBarItem(new ItemBuilder(Material.IRON_SWORD).name("Unranked").unbreakable().build(),
                        ((player, interact) -> queueMenuProvider.provide(MatchType.UNRANKED, new SoloTeam(player)).openMenu(player.getPlayer())))));
        add(ItemBarType.QUEUE, new ItemBar(
                new ItemBarItem(new ItemBuilder(Material.REDSTONE).name("Leave Queue").unbreakable().build(),
                        ((player, interact) -> {
                            queueManager.remove(player);
                            apply(ItemBarType.LOBBY, player);
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
