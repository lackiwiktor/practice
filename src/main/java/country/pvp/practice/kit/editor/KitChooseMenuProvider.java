package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.data.Callback;
import country.pvp.practice.kit.KitChooseMenu;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.ladder.LadderManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitChooseMenuProvider {

    private final LadderManager ladderManager;

    public KitChooseMenu provide(Callback<Ladder> callback) {
        return new KitChooseMenu(ladderManager, callback);
    }
}
