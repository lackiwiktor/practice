package country.pvp.practice.team;

import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;

import java.util.Set;

public interface Team {

    int size();

    Set<PracticePlayer> getPlayers();

    default boolean hasPlayer(PracticePlayer player) {
        return getPlayers().contains(player);
    }

    default boolean setPlayersState(PlayerState state) {
        getPlayers().forEach(it -> it.setState(state));
    }
}
