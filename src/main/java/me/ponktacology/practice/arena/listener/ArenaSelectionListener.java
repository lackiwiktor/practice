package me.ponktacology.practice.arena.listener;

import me.ponktacology.practice.arena.match.StateSelectionData;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaSelectionListener extends PracticePlayerListener {

    @EventHandler
    public void blockInteractEvent(PlayerInteractEvent event) {
        PracticePlayer session = get(event);
        if (!session.isSelecting()) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.GOLD_PICKAXE) return;

        StateSelectionData selectionData = session.getStateData();
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
                Messenger.messageSuccess(session, "Set first corner of selection.");
                selectionData.setFirst(event.getClickedBlock().getLocation());
                event.setCancelled(true);
                break;
            case RIGHT_CLICK_BLOCK:
                Messenger.messageSuccess(session, "Set second corner of selection.");
                selectionData.setSecond(event.getClickedBlock().getLocation());
                event.setCancelled(true);
                break;
            default:
                return;
        }
        if (selectionData.isReady()) {
            Messenger.messageSuccess(session, "You successfully selected area, bind it to a arena using /arena region");
        }
    }
}
