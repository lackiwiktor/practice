package country.pvp.practice.match;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class MatchKitListener extends PlayerListener {

    @Inject
    public MatchKitListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
    public void dropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        PracticePlayer practicePlayer = get(event);
        if (!practicePlayer.isInMatch()) return;

        MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        practicePlayer.getMatchingKit(match.getLadder(), item).ifPresent(it -> event.setCancelled(true));
    }

    @EventHandler(ignoreCancelled = true)
    public void clickEvent(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        PracticePlayer practicePlayer = get((Player) event.getWhoClicked());
        if (!practicePlayer.isInMatch()) return;

        MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        practicePlayer.getMatchingKit(match.getLadder(), item).ifPresent(it -> event.setCancelled(true));
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent event) {
        PracticePlayer practicePlayer = get(event.getEntity());
        if (!practicePlayer.isInMatch()) return;

        MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
        Match match = matchData.getMatch();
        event.getDrops().removeIf(it -> practicePlayer.getMatchingKit(match.getLadder(), it).isPresent());
    }
}
