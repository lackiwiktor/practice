package country.pvp.practice.match;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PracticePlayer;
import lombok.Data;

@Data
public class RematchData {

    private final PracticePlayer player;
    private final Ladder ladder;

    @Override
    public String toString() {
        return "RematchData{" +
                "player=" + player.getName() +
                "ladder=" + ladder.getName() +
                '}';
    }
}
