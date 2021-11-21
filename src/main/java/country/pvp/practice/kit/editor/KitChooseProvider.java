package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.ladder.LadderManager;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitChooseProvider {

    private final LadderManager ladderManager;
    private final KitEditorService kitEditorService;

    public KitChooseMenu provide(PracticePlayer player) {
        return new KitChooseMenu(player, ladderManager, kitEditorService);
    }
}
