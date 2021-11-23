package country.pvp.practice.player.data;

public class PlayerStateData {

    private PlayerData data;

    public void setStateData(PlayerData stateData) {
        data = stateData;
    }

    public void removeStateData() {
        data = null;
    }

    public <V extends PlayerData> V get() {
        return (V) data;
    }

    public boolean hasStateData() {
        return data != null;
    }
}
