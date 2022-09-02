package me.ponktacology.practice.invitation.duel;

import me.ponktacology.practice.Service;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.invitation.Invitation;
import me.ponktacology.practice.invitation.InvitationService;
import me.ponktacology.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DuelService<V extends DuelInvitable, D extends DuelRequest<V>> extends Service {

    protected abstract void handleAccept(V inviter, V invitee, Ladder ladder);

    protected abstract boolean canSendDuel(V inviter, V invitee);

    protected abstract boolean canAcceptDuel(V inviter, V invitee);

    protected abstract D createDuelRequest(V inviter, Ladder ladder);

    public void invite(V inviter, V invitee, Ladder ladder, String message) {
        if (!canSendDuel(inviter, invitee)) return;

        if (inviter.hasDuelRequest(invitee)) {
            DuelRequest duelRequest = inviter.getDuelRequest(invitee);

            if (ladder.equals(duelRequest.getLadder())) {
                acceptInvite0(inviter, invitee, ladder);
                return;
            }
        }

        Invitation invitation = new Invitation(message, invitee) {
            @Override
            protected boolean onAccept() {
                return acceptInvite(inviter, invitee, ladder);
            }

            @Override
            protected void onDecline() {
                invitee.clearDuelRequests(inviter);
            }
        };

        invitee.addDuelRequest(createDuelRequest(inviter, ladder));
        Practice.getService(InvitationService.class).invite(invitee, invitation);
    }

    private boolean acceptInvite0(V inviter, V invitee, Ladder ladder) {
        boolean canAcceptDuel = canAcceptDuel(inviter, invitee);

        if (!canAcceptDuel) return true;

        inviter.clearDuelRequests(invitee);
        invitee.clearDuelRequests(inviter);

        handleAccept(inviter, invitee, ladder);

        return canAcceptDuel;
    }

    public boolean acceptInvite(V invitee, D request) {
        return acceptInvite(request.getInviter(), invitee, request.getLadder());
    }

    public boolean acceptInvite(V inviter, V invitee, Ladder ladder) {
        return acceptInvite0(inviter, invitee, ladder);
    }
}
