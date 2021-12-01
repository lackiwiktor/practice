package country.pvp.practice.duel;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PracticePlayer;
import lombok.Getter;

@Getter
public class PlayerDuelRequest extends DuelRequest {

    private final PracticePlayer inviter;

    public PlayerDuelRequest(Ladder ladder, PracticePlayer inviter) {
        super(ladder);
        this.inviter = inviter;
    }
}
