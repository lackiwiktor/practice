package me.ponktacology.practice.party.duel;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.invitation.duel.DuelService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.PartyTeam;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.util.message.Messenger;

public class PartyDuelService extends DuelService<Party, PartyDuelRequest> {


  @Override
  protected void handleAccept(Party inviter, Party invitee, Ladder ladder) {
    Practice.getService(MatchService.class).start(ladder, false, true, PartyTeam.of(inviter), PartyTeam.of(invitee));
  }

  @Override
  protected boolean canSendDuel(Party inviter, Party invitee) {
    return check(inviter, invitee);
  }

  @Override
  protected boolean canAcceptDuel(Party inviter, Party invitee) {
    return check(inviter, invitee);
  }

  @Override
  protected PartyDuelRequest createDuelRequest(Party inviter, Ladder ladder) {
    return new PartyDuelRequest(inviter, ladder);
  }

  private boolean check(Party inviter, Party invitee) {
    if (inviter.equals(invitee)) {
      Messenger.messageError(inviter, "You can't duel your own party.");
      return false;
    }

    if (!inviter.isInLobby()) {
      Messenger.messageError(inviter, "You must be in lobby if you want to duel someone.");
      return false;
    }

    if (!invitee.isInLobby()) {
      Messenger.messageError(inviter, "This party is busy right now.");
      return false;
    }

    return true;
  }
}
