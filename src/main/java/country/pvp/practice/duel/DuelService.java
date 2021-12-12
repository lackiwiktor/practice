package country.pvp.practice.duel;

import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.ladder.Ladder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class DuelService<V extends DuelInvitable, D extends DuelRequest<V>> {

    private final InvitationService invitationService;

    protected abstract void handleAccept(V inviter, V invitee, Ladder ladder);

    protected abstract boolean canSendDuel(V inviter, V invitee);

    protected abstract boolean canAcceptDuel(V inviter, V invitee);

    protected abstract D createDuelRequest(V inviter, Ladder ladder);

    public void invite(V inviter, V invitee, Ladder ladder, String message) {
        System.out.println("XD??");
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
            protected void onAccept() {
                acceptInvite(inviter, invitee, ladder);
            }

            @Override
            protected void onDecline() {
                invitee.clearDuelRequests(inviter);
            }
        };

        invitee.addDuelRequest(createDuelRequest(inviter, ladder));
        invitationService.invite(invitee, invitation);
    }

    private void acceptInvite0(V inviter, V invitee, Ladder ladder) {
        if (!canAcceptDuel(inviter, invitee)) return;

        inviter.clearDuelRequests(invitee);
        invitee.clearDuelRequests(inviter);

        handleAccept(inviter, invitee, ladder);
    }

    public void acceptInvite(V invitee, D request) {
        acceptInvite(request.getInviter(), invitee, request.getLadder());
    }

    public void acceptInvite(V inviter, V invitee, Ladder ladder) {
        acceptInvite0(inviter, invitee, ladder);
    }
}
