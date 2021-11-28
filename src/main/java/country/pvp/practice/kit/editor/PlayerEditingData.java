package country.pvp.practice.kit.editor;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.data.PlayerData;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
class PlayerEditingData implements PlayerData {
    private final Ladder ladder;
}
