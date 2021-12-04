package country.pvp.practice.lobby;

import country.pvp.practice.match.RematchData;
import country.pvp.practice.player.data.SessionData;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class SessionLobbyData implements SessionData {
    private @Nullable final RematchData rematchData;
}
