package me.ponktacology.practice.match.team.type;

import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Data(staticConstructor = "of")
public class SoloTeam extends Team {

    private final PracticePlayer practicePlayer;

    private SoloTeam(PracticePlayer player) {
        super();
        this.practicePlayer = player;
        this.players.add(player);
    }

    @Override
    public String getName() {
        return practicePlayer.getName();
    }

    public int getPing() {
        if (!practicePlayer.isOnline()) return -1;
        return practicePlayer.getPing();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoloTeam soloTeam = (SoloTeam) o;
        return Objects.equals(practicePlayer, soloTeam.practicePlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(practicePlayer);
    }
}
