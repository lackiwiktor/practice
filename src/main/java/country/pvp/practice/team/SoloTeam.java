package country.pvp.practice.team;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
public class SoloTeam extends Team {

    private final @NotNull PracticePlayer player;

    @Override
    public @NotNull Set<PracticePlayer> getPlayers() {
        return Collections.singleton(player);
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    public int getElo(Ladder ladder) {
        return player.getElo(ladder);
    }

    @Override
    public void setElo(Ladder ladder, int elo) {
         player.setElo(ladder, elo);
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public boolean equals(@Nullable Object o) {
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
