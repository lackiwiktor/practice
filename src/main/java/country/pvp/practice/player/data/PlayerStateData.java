package country.pvp.practice.player.data;

import org.jetbrains.annotations.Nullable;

public class PlayerStateData {

    private @Nullable PlayerData data;

    public void setStateData(PlayerData stateData) {
        data = stateData;
    }

    public void removeStateData() {
        data = null;
    }

    public <V extends PlayerData> @Nullable V get() {
        return (V) data;
    }

    public boolean hasStateData() {
        return data != null;
    }
}
