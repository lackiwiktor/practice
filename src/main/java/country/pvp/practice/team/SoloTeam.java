package country.pvp.practice.team;

import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class SoloTeam extends Team {

    private final PracticePlayer player;

    @Override
    public Set<PracticePlayer> getPlayers() {
        return Collections.singleton(player);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoloTeam soloTeam = (SoloTeam) o;
        return Objects.equals(player, soloTeam.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
