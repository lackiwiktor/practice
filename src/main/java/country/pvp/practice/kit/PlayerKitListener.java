package country.pvp.practice.kit;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class PlayerKitListener extends PlayerListener {

    @Inject
    public PlayerKitListener(PlayerManager playerManager) {
        super(playerManager);
    }

    @EventHandler
    public void clickEvent(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        PlayerSession playerSession = get(event);
        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();

        playerSession.getMatchingKit(match.getLadder(), item).ifPresent(it -> {
            Messager.message(playerSession, Messages.MATCH_PLAYER_EQUIP_KIT.match("{kit}", it.getName()));
            it.apply(playerSession);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void dropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        PlayerSession playerSession = get(event);

        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();

        playerSession.getMatchingKit(match.getLadder(), item).ifPresent(it -> event.setCancelled(true));
    }

    @EventHandler(ignoreCancelled = true)
    public void clickEvent(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;

        PlayerSession playerSession = get((Player) event.getWhoClicked());
        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();

        playerSession.getMatchingKit(match.getLadder(), item).ifPresent(it -> event.setCancelled(true));
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent event) {
        PlayerSession playerSession = get(event.getEntity());
        if (!playerSession.isInMatch()) return;

        Match match = playerSession.getCurrentMatch();

        event.getDrops().removeIf(it -> playerSession.getMatchingKit(match.getLadder(), it).isPresent());
    }
}
