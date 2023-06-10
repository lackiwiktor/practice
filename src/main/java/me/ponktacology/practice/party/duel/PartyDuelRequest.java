package me.ponktacology.practice.party.duel;

import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.invitation.duel.DuelRequest;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.party.Party;
import org.jetbrains.annotations.Nullable;

public class PartyDuelRequest extends DuelRequest<Party> {
    public PartyDuelRequest(Party inviter, Ladder ladder, @Nullable MatchArena arena) {
        super(inviter, ladder, arena);
    }
}
