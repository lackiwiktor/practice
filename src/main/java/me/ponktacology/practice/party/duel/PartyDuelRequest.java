package me.ponktacology.practice.party.duel;

import me.ponktacology.practice.invitation.duel.DuelRequest;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.party.Party;

public class PartyDuelRequest extends DuelRequest<Party> {
    public PartyDuelRequest(Party inviter, Ladder ladder) {
        super(inviter, ladder);
    }
}
