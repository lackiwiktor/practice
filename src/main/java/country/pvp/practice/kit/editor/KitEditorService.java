package country.pvp.practice.kit.editor;

import com.google.inject.Inject;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.settings.PracticeSettings;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class KitEditorService {

    private final @NotNull VisibilityUpdater visibilityUpdater;
    private final @NotNull PracticeSettings practiceSettings;

    public void moveToEditor(@NotNull PracticePlayer player, @NotNull Ladder ladder) {
        player.setState(PlayerState.EDITING_KIT, new PlayerEditingData(ladder));
        PlayerUtil.resetPlayer(player.getPlayer());
        visibilityUpdater.update(player);
        player.teleport(practiceSettings.getEditorLocation());
        ladder.getKit().apply(player);
    }
}
