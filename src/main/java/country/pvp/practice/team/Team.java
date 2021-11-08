package country.pvp.practice.team;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;
import org.bukkit.Location;

import java.util.Set;

public interface Team {

    int size();

    Set<PracticePlayer> getPlayers();

    default boolean hasPlayer(PracticePlayer player) {
        return getPlayers().contains(player);
    }

    default void setPlayersState(PlayerState state) {
        for (PracticePlayer it : getPlayers()) {
            it.setState(state);
        }
    }

    default void teleport(Location location) {
        for (PracticePlayer it : getPlayers()) {
            it.teleport(location);
        }
    }

    default void applyKits(Ladder ladder) {

    }
}
