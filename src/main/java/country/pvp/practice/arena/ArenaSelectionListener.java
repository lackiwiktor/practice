package country.pvp.practice.arena;

import com.google.inject.Inject;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Sender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class ArenaSelectionListener extends PlayerListener {

    @Inject
    public ArenaSelectionListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler
    public void blockInteractEvent(PlayerInteractEvent event) {
        PlayerSession session = get(event);

        if (!session.isSelecting()) return;

        SessionSelectionData selectionData = session.getStateData();

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                Sender.messageSuccess(session, "Set first corner of selection.");
                selectionData.setFirst(event.getClickedBlock().getLocation());
                event.setCancelled(true);
                break;
            case RIGHT_CLICK_BLOCK:
                Sender.messageSuccess(session, "Set second corner of selection.");
                selectionData.setSecond(event.getClickedBlock().getLocation());
                event.setCancelled(true);
                break;
            default:
                return;
        }

        if (selectionData.isReady()) {
            Sender.messageSuccess(session, "You successfully selected area, bind it to a arena using /arena region");
        }
    }
}
