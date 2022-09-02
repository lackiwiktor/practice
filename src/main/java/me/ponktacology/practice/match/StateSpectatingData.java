package me.ponktacology.practice.match;

import me.ponktacology.practice.player.data.StateData;
import lombok.Data;

@Data
public class StateSpectatingData implements StateData {
    private final Match match;
}
