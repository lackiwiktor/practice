package country.pvp.practice.match;

import country.pvp.practice.player.data.SessionData;
import lombok.Data;

@Data
public class SessionSpectatingData implements SessionData {
    private final Match match;
}
