package country.pvp.practice.match.command;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SpectateCommand {

    private final PlayerManager playerManager;

    @Command("spectate")
    public void specate(@Sender Player sender, @Name("player") PracticePlayer player) {
        PracticePlayer practicePlayer = playerManager.get(sender);

        if (!practicePlayer.isInLobby()) {
            Messager.messageError(practicePlayer, "You must be in lobby in order to spectate someone.");
            return;
        }

        if (!player.isInMatch()) {
            Messager.messageError(practicePlayer, "This player is not in a match right now.");
            return;
        }

        Match match = player.getCurrentMatch();
        match.startSpectating(practicePlayer, player);
    }
}
