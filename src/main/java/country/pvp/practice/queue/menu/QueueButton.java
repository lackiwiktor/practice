package country.pvp.practice.queue.menu;

import country.pvp.practice.itembar.ItemBuilder;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.menu.Button;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.queue.Queue;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class QueueButton extends Button {

    private final MatchManager matchManager;
    private final PlayerSession playerSession;
    private final Queue queue;

    @Override
    public ItemStack getButtonItem(Player player) {
        Ladder ladder = queue.getLadder();

        return new ItemBuilder(ladder.getIcon())
                .name(ladder.getDisplayName())
                .lore("",
                        ChatColor.GRAY + "In Fights: " + ChatColor.WHITE + matchManager.getPlayersInFightCount(queue.getLadder(), queue.isRanked()),
                        ChatColor.GRAY + "In Queue: " + ChatColor.WHITE + queue.size())
                .build();
    }

    @Override
    public void clicked( Player player, ClickType clickType) {
        queue.addPlayer(playerSession);
        player.getOpenInventory().close();
    }
}
