package country.pvp.practice.queue.menu;

import com.google.common.collect.Maps;
import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.menu.Button;
import country.pvp.practice.menu.Menu;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.queue.Queue;
import country.pvp.practice.queue.QueueManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class QueueMenu extends Menu {

    private final QueueManager queueManager;
    private final MatchManager matchManager;

    private final boolean ranked;
    private final PlayerSession playerSession;

    @Override
    public String getTitle(Player player) {
        return "Select kit...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Queue queue : queueManager.getQueues(ranked)) {
            buttons.put(buttons.size(), new QueueButton(queue));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private class QueueButton extends Button {

        private final Queue queue;

        @Override
        public ItemStack getButtonItem(Player player) {
            Ladder ladder = queue.getLadder();
            return new ItemBuilder(ladder.getIcon())
                    .name(ladder.getDisplayName())
                    .lore("",
                            ChatColor.GRAY + "In Fights: " + ChatColor.WHITE + matchManager.getPlayersInFightCount(ladder, ranked),
                            ChatColor.GRAY + "In Queue: " + ChatColor.WHITE + queue.size())
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            queue.addPlayer(playerSession);
            player.getOpenInventory().close();
        }
    }
}
