package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerRepository;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitEditorMenuProvider {

    private final PlayerRepository playerRepository;

    public KitEditorMenu provide(PlayerSession player, Ladder ladder) {
        return new KitEditorMenu(playerRepository, player, ladder);
    }
}
