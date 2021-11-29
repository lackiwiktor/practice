package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitEditorMenuProvider {

    private final PlayerService playerService;

    public KitEditorMenu provide(PracticePlayer player, Ladder ladder) {
        return new KitEditorMenu(playerService, player, ladder);
    }
}
