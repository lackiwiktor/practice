package country.pvp.practice.kit;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchData;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerKitListener extends PlayerListener {

    @Inject
    public PlayerKitListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void clickEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PracticePlayer practicePlayer = get(event);
        if (!practicePlayer.isInMatch()) return;

        MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        practicePlayer.getMatchingKit(match.getLadder(), item).ifPresent(it -> it.apply(practicePlayer));
    }

    @EventHandler(ignoreCancelled = true)
    public void dropEvent(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        PracticePlayer practicePlayer = get(event);
        if (!practicePlayer.isInMatch()) return;

        MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        practicePlayer.getMatchingKit(match.getLadder(), item).ifPresent(it -> event.setCancelled(true));
    }

    @EventHandler(ignoreCancelled = true)
    public void dropEvent(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        PracticePlayer practicePlayer = get((Player) event.getWhoClicked());
        if (!practicePlayer.isInMatch()) return;

        MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        practicePlayer.getMatchingKit(match.getLadder(), item).ifPresent(it -> event.setCancelled(true));
    }
}
