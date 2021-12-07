package country.pvp.practice.commands;

import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PlayerCommands {

    private final PlayerManager playerManager;

    public PlayerSession get(Player player) {
        return playerManager.get(player);
    }
}
