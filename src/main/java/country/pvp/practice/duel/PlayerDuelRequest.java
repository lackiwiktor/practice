package country.pvp.practice.duel;

import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerSession;
import lombok.Getter;

@Getter
public class PlayerDuelRequest extends DuelRequest {

    private final PlayerSession inviter;

    public PlayerDuelRequest(Ladder ladder, PlayerSession inviter) {
        super(ladder);
        this.inviter = inviter;
    }
}
