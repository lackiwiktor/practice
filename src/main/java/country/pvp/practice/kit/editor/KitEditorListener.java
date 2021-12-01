package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.player.PlayerListener;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class KitEditorListener extends PlayerListener {

    private final PlayerService playerService;
    private final LobbyService lobbyService;

    @Inject
    public KitEditorListener(PlayerManager playerManager, PlayerService playerService, LobbyService lobbyService) {
        super(playerManager);
        this.playerService = playerService;
        this.lobbyService = lobbyService;
    }

    @EventHandler(ignoreCancelled = true)
    public void interactEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        PracticePlayer practicePlayer = get(event);

        if (!practicePlayer.isInEditor()) return;

        Ladder ladder = practicePlayer.getCurrentlyEditingKit();

        switch (event.getClickedBlock().getType()) {
            case CHEST:
                new KitEditorChest(ladder).openMenu(event.getPlayer());
                break;
            case ANVIL:
                new KitEditorMenu(playerService, practicePlayer, ladder).openMenu(event.getPlayer());
                break;
            case WOODEN_DOOR:
            case WOOD_DOOR:
            case SIGN_POST:
                lobbyService.moveToLobby(practicePlayer);
                break;
        }

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void dropItem(PlayerDropItemEvent event) {
        PracticePlayer practicePlayer = get(event);

        if (practicePlayer.isInEditor()) {
            event.getItemDrop().remove();
        }
    }
}
