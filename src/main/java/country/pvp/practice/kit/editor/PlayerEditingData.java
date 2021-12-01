package country.pvp.practice.kit.editor;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.data.PlayerData;
import lombok.Data;

@Data
public class PlayerEditingData implements PlayerData {
    private final Ladder ladder;
}
