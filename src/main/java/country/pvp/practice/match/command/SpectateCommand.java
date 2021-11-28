package country.pvp.practice.match.command;

import com.google.inject.Inject;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.annotation.Command;
import me.vaperion.blade.command.annotation.Name;
import me.vaperion.blade.command.annotation.Sender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SpectateCommand {

    private final @NotNull PlayerManager playerManager;

    @Command("spectate")
    public void specate(@Sender @NotNull Player sender, @Name("player") @NotNull PracticePlayer player) {
        PracticePlayer practicePlayer = playerManager.get(sender);

        if (!practicePlayer.isInLobby()) {
            Messager.messageError(practicePlayer, "You must be in lobby in order to spectate someone.");
            return;
        }

        if (!player.isInMatch()) {
            Messager.messageError(practicePlayer, "This player is not in a match right now.");
            return;
        }

        PlayerMatchData matchData = player.getStateData();
        Match match = matchData.getMatch();
        match.startSpectating(practicePlayer, player);
    }
}
