package country.pvp.practice.arena.command.provider;

import com.google.inject.Inject;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.arena.ArenaManager;
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
public class ArenaProvider implements BladeProvider<Arena> {

    private final ArenaManager arenaManager;

    @Override
    public @Nullable Arena provide(@NotNull BladeContext context, @NotNull BladeParameter parameter, @Nullable String input) throws BladeExitMessage {
        if (input == null) return null;

        return Optional.ofNullable(arenaManager.get(input)).orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Wrong arena"));
    }
}
