package me.ponktacology.practice.arena.match;

import me.ponktacology.practice.player.data.StateData;
import me.ponktacology.practice.util.Selection;
import lombok.Getter;
import org.bukkit.Location;

@Getter
public class StateSelectionData implements StateData {

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
