package me.ponktacology.practice.player.duel;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.invitation.duel.DuelService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;

public class PlayerDuelService extends DuelService<PracticePlayer, PlayerDuelRequest> {

  @Override
  protected void handleAccept(PracticePlayer inviter, PracticePlayer invitee, Ladder ladder) {
    Practice.getService(MatchService.class)
        .start(ladder, false, true, SoloTeam.of(inviter), SoloTeam.of(invitee));
  }

  @Override
  protected boolean canSendDuel(PracticePlayer inviter, PracticePlayer invitee) {
    if (invitee.hasDuelRequest(inviter)) {
      Messenger.messageError(inviter, "You already invited this player for a duel.");
      return false;
    }

    return check(inviter, invitee);
  }

  @Override
  protected boolean canAcceptDuel(PracticePlayer inviter, PracticePlayer invitee) {
    return check(inviter, invitee);
  }

  private boolean check(PracticePlayer inviter, PracticePlayer invitee) {
    if (inviter.isInEvent()) {
      Messenger.messageError(inviter, "You can't be in an event if you want to duel a player.");
      return false;
    }

    if (invitee.isInEvent()) {
      Messenger.messageError(inviter, "Player can't be in an event if you want to duel him.");
      return false;
    }

    if (invitee.hasParty()) {
      Messenger.messageError(inviter, "Player can't be in a party if you want to duel him.");
      return false;
    }

    if (inviter.hasParty()) {
      Messenger.messageError(inviter, "You can't be in a party if you want to duel a player.");
      return false;
    }

    if (!inviter.isInLobby()) {
      Messenger.messageError(inviter, "You must be in lobby if you want to duel a player.");
      return false;
    }

    if (!invitee.isInLobby()) {
      Messenger.messageError(inviter, "This player is busy right now.");
      return false;
    }

    return true;
  }

  @Override
  protected PlayerDuelRequest createDuelRequest(PracticePlayer inviter, Ladder ladder) {
    return new PlayerDuelRequest(inviter, ladder);
  }
}
