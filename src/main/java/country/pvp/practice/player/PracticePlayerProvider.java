package country.pvp.practice.player;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import me.vaperion.blade.command.argument.BladeProvider;
import me.vaperion.blade.command.container.BladeParameter;
import me.vaperion.blade.command.context.BladeContext;
import me.vaperion.blade.command.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PracticePlayerProvider implements BladeProvider<PracticePlayer> {

    private final PlayerManager playerManager;

    @Override
    public @Nullable PracticePlayer provide(@NotNull BladeContext context, @NotNull BladeParameter parameter, @Nullable String input) throws BladeExitMessage {
        if(input == null) return null;

        return playerManager.get(input).orElseThrow(() -> new BladeExitMessage(ChatColor.RED + "Error: Player not found."));
    }
}
