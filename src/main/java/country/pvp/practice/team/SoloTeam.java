package country.pvp.practice.team;

import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class SoloTeam implements Team {

    private final PracticePlayer player;

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Set<PracticePlayer> getPlayers() {
        return Collections.singleton(player);
    }

}
