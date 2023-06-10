package me.ponktacology.practice.invitation.duel;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.invitation.Invitation;
import me.ponktacology.practice.invitation.InvitationService;
import me.ponktacology.practice.ladder.Ladder;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public abstract class DuelService<V extends DuelInvitable, D extends DuelRequest<V>>
    extends Service {

  protected abstract void handleAccept(
      V inviter, V invitee, Ladder ladder, @Nullable MatchArena arena);

  protected abstract boolean canSendDuel(V inviter, V invitee);

  protected abstract boolean canAcceptDuel(V inviter, V invitee);

  protected abstract D createDuelRequest(V inviter, Ladder ladder, @Nullable MatchArena arena);

  // If arena is null, choose random
  public void invite(
      V inviter, V invitee, Ladder ladder, @Nullable MatchArena arena, String message) {
    if (!canSendDuel(inviter, invitee)) return;

    if (inviter.hasDuelRequest(invitee)) {
      DuelRequest duelRequest = inviter.getDuelRequest(invitee);

      if (ladder.equals(duelRequest.getLadder())) {
        acceptInvite0(inviter, invitee, ladder, arena);
        return;
      }
    }

    Invitation invitation =
        new Invitation(message, invitee) {
          @Override
          protected boolean onAccept() {
            return acceptInvite(inviter, invitee, ladder, arena);
          }

          @Override
          protected void onDecline() {
            invitee.clearDuelRequests(inviter);
          }
        };

    invitee.addDuelRequest(createDuelRequest(inviter, ladder, arena));
    Practice.getService(InvitationService.class).invite(invitee, invitation);
  }

  private boolean acceptInvite0(V inviter, V invitee, Ladder ladder, MatchArena arena) {
    boolean canAcceptDuel = canAcceptDuel(inviter, invitee);

    if (!canAcceptDuel) return true;

    inviter.clearDuelRequests(invitee);
    invitee.clearDuelRequests(inviter);

    handleAccept(inviter, invitee, ladder, arena);

    return canAcceptDuel;
  }

  public boolean acceptInvite(V invitee, D request) {
    return acceptInvite(
        request.getInviter(), invitee, request.getLadder(), request.getArena());
  }

  public boolean acceptInvite(V inviter, V invitee, Ladder ladder, MatchArena arena) {
    return acceptInvite0(inviter, invitee, ladder, arena);
  }
}
