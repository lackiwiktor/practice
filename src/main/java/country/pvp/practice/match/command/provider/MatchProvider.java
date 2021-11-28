package country.pvp.practice.match.command.provider;

import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.argument.BladeProvider;
import me.vaperion.blade.command.container.BladeParameter;
import me.vaperion.blade.command.context.BladeContext;
import me.vaperion.blade.command.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@RequiredArgsConstructor
public class MatchProvider implements BladeProvider<Match> {

    private final PlayerManager playerManager;
    private final MatchManager matchManager;

    @Override
    public @Nullable Match provide( BladeContext context, BladeParameter parameter, @Nullable String input) throws BladeExitMessage {
        if (input == null) return null;

        Optional<PracticePlayer> practicePlayerOptional = playerManager.get(input);

        if (practicePlayerOptional.isPresent()) {
            PracticePlayer practicePlayer = practicePlayerOptional.get();

            if (practicePlayer.isInMatch()) {
                return practicePlayer.<PlayerMatchData>getStateData().getMatch();
            }

            throw new BladeExitMessage(ChatColor.RED + "Error: ");
        } else throw new BladeExitMessage(ChatColor.RED + "Error: This player is not online.");
    }
}
