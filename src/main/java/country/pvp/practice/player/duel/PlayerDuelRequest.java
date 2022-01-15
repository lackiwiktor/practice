package country.pvp.practice.player.duel;

import country.pvp.practice.duel.DuelRequest;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerSession;

public class PlayerDuelRequest extends DuelRequest<PlayerSession> {

   // private @Nullable Arena arena;

    public PlayerDuelRequest(PlayerSession inviter, Ladder ladder) {
        super(inviter, ladder);
    }
}
