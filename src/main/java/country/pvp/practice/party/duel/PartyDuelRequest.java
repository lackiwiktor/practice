package country.pvp.practice.party.duel;

import country.pvp.practice.duel.DuelRequest;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.party.Party;

public class PartyDuelRequest extends DuelRequest<Party> {
    public PartyDuelRequest(Party inviter, Ladder ladder) {
        super(inviter, ladder);
    }
}
