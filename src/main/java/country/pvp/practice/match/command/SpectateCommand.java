package country.pvp.practice.match.command;

import com.google.inject.Inject;
import country.pvp.practice.command.PlayerCommand;
import country.pvp.practice.match.Match;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

public class SpectateCommand extends PlayerCommand {

    @Inject
    public SpectateCommand(PlayerManager playerManager) {
        super(playerManager);
    }

    @Command("spectate")
    public void specate(@Sender Player sender, @Name("player") PlayerSession player) {
        PlayerSession playerSession = get(sender);

        if (!playerSession.isInLobby()) {
            Messager.messageError(playerSession, "You must be in lobby in order to spectate someone.");
            return;
        }

        if (!player.isInMatch()) {
            Messager.messageError(playerSession, "This player is not in a match right now.");
            return;
        }

        Match match = player.getCurrentMatch();
        match.startSpectating(playerSession, player);
    }
}
