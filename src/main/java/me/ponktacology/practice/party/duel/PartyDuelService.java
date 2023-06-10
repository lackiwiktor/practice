package me.ponktacology.practice.party.duel;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.PracticePreconditions;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.invitation.duel.DuelService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.PartyTeam;
import me.ponktacology.practice.party.Party;
import org.jetbrains.annotations.Nullable;

public class PartyDuelService extends DuelService<Party, PartyDuelRequest> {


  @Override
  protected void handleAccept(Party inviter, Party invitee, Ladder ladder, @Nullable MatchArena arena) {
    Practice.getService(MatchService.class).start(ladder, arena, false, true, PartyTeam.of(inviter), PartyTeam.of(invitee));
  }

  @Override
  protected boolean canSendDuel(Party inviter, Party invitee) {
    return PracticePreconditions.canSendPartyDuel(inviter, invitee);
  }

  @Override
  protected boolean canAcceptDuel(Party inviter, Party invitee) {
    return PracticePreconditions.canAcceptPartyDuel(inviter, invitee);
  }

  @Override
  protected PartyDuelRequest createDuelRequest(Party inviter, Ladder ladder, @Nullable MatchArena arena) {
    return new PartyDuelRequest(inviter, ladder, arena);
  }
}
