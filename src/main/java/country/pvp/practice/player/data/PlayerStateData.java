package country.pvp.practice.player.data;

import com.google.common.collect.Maps;

import java.util.Map;

public class PlayerStateData {

    private final Map<PlayerState, Object> stateDataMap = Maps.newHashMap();

    public void setStateData(PlayerState state, Object stateData) {
        stateDataMap.put(state, stateData);
    }

    public void removeStateData(PlayerState state) {
        stateDataMap.remove(state);
    }

    public <V> V getStateData(PlayerState state) {
        return (V) stateDataMap.get(state);
    }

    public boolean hasStateData(PlayerState state) {
        return stateDataMap.containsKey(state);
    }

    public void clear() {
        stateDataMap.clear();
    }
}
