package country.pvp.practice.arena;

import country.pvp.practice.player.data.SessionData;
import country.pvp.practice.util.Selection;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class SessionSelectionData implements SessionData {

    private final Selection selection = new Selection();

    public void setFirst(Location location) {
        selection.setFirst(location);
    }

    public void setSecond(Location location) {
        selection.setSecond(location);
    }

    public boolean isReady() {
        return selection.isReady();
    }
}
