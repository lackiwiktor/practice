package country.pvp.practice.kit;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchData;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PlayerKitListener implements Listener {

    private final PlayerManager playerManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void clickEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        PracticePlayer practicePlayer = playerManager.get(player);

        if (practicePlayer.isFighting()) {
            MatchData matchData = practicePlayer.getStateData(PlayerState.IN_MATCH);
            Match match = matchData.getMatch();
            practicePlayer.getMatchingKit(match.getLadder(), item).apply(practicePlayer);
        }
    }
}
