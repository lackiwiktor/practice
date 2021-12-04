package country.pvp.practice.kit.editor;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.data.SessionData;
import lombok.Data;

@Data
public class SessionEditingData implements SessionData {
    private final Ladder ladder;
}
