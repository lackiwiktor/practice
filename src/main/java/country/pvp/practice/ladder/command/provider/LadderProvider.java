package country.pvp.practice.ladder.command.provider;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.argument.BladeProvider;
import me.vaperion.blade.command.container.BladeParameter;
import me.vaperion.blade.command.context.BladeContext;
import me.vaperion.blade.command.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LadderProvider implements BladeProvider<Ladder> {

    private final @NotNull LadderManager ladderManager;

    @Override
    public @Nullable Ladder provide(@NotNull BladeContext context, @NotNull BladeParameter parameter, @Nullable String input) throws BladeExitMessage {
        if (input == null) return null;

        return Optional.ofNullable(ladderManager.get(input)).orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Wrong ladder"));
    }
}
