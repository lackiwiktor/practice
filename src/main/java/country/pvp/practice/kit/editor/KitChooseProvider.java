package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitChooseProvider {

    private final @NotNull LadderManager ladderManager;
    private final @NotNull PlayerService playerService;
    private final @NotNull KitEditorService kitEditorService;

    public @NotNull KitChooseMenu provide(PracticePlayer player) {
        return new KitChooseMenu(ladderManager, kitEditorService, player);
    }
}
