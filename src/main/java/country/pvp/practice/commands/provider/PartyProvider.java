package country.pvp.practice.commands.provider;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import country.pvp.practice.party.Party;
import country.pvp.practice.player.PlayerManager;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.argument.BladeProvider;
import me.vaperion.blade.command.container.BladeParameter;
import me.vaperion.blade.command.context.BladeContext;
import me.vaperion.blade.command.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyProvider implements BladeProvider<Party> {

    private final PlayerManager playerManager;

    @Override
    public @Nullable Party provide(BladeContext context, BladeParameter parameter, @Nullable String input) throws BladeExitMessage {
        if (Strings.isNullOrEmpty(input) || "null".equals(input)) return null;

        PlayerSession session = playerManager
                .get(input)
                .orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Couldn't find a party with this player."));

        if (!session.hasParty()) {
            new BladeExitMessage(ChatColor.RED + "Error: This player doesn't have a party.");
        }

        return session.getParty();
    }

    @Override
    public @NotNull List<String> suggest(@NotNull BladeContext context, @NotNull String input) throws BladeExitMessage {
        return playerManager
                .getAll()
                .stream()
                .map(PlayerSession::getName)
                .filter(name -> name.startsWith(input))
                .collect(Collectors.toList());
    }
}
