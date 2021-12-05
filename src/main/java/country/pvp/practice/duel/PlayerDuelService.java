package country.pvp.practice.duel;

import com.google.inject.Inject;
import country.pvp.practice.invitation.Invitation;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PlayerDuelService {

    private final InvitationService invitationService;
    private final MatchProvider matchProvider;

    public void inviteForDuel(PlayerSession inviter, PlayerSession invitee, Ladder ladder) {
        if (!inviter.isInLobby()) {
            Messager.messageError(inviter, "You must be in lobby in order to duel someone.");
            return;
        }

        if (!invitee.isInLobby()) {
            Messager.messageError(inviter, "This player is busy right now.");
            return;
        }

        if (invitee.hasDuelRequest(inviter)) {
            Messager.messageError(inviter, "You already invited this player for a duel.");
            return;
        }

        if (inviter.hasDuelRequest(invitee)) {
            DuelRequest duelRequest = inviter.getDuelRequest(invitee);

            if (ladder.equals(duelRequest.getLadder())) {
                acceptInvite0(inviter, invitee, ladder);
                return;
            }
        }

        Invitation invitation = new Invitation("You have been invited to a duel by " + inviter.getName() + ".", invitee) {
            @Override
            protected void onAccept() {
                acceptInvite(inviter, invitee, ladder);
            }

            @Override
            protected void onDecline() {
                invitee.clearDuelRequests(inviter);
            }
        };

        invitee.addDuelRequest(new PlayerDuelRequest(ladder, inviter));
        invitationService.invite(invitee, invitation);
    }

    public void acceptInvite(PlayerSession invitee, PlayerDuelRequest request) {
        acceptInvite(request.getInviter(), invitee, request.getLadder());
    }

    public void acceptInvite0(PlayerSession inviter, PlayerSession invitee, Ladder ladder) {
        if (!invitee.isInLobby()) {
            Messager.messageError(invitee, "You must be in lobby in order to duel someone.");
            return;
        }

        if (!inviter.isInLobby()) {
            Messager.messageError(invitee, "This player is busy right now.");
            return;
        }

        invitee.clearDuelRequests(inviter);
        matchProvider.provide(ladder, false, true, SoloTeam.of(inviter), SoloTeam.of(invitee)).init();
    }

    public void acceptInvite(PlayerSession inviter, PlayerSession invitee, Ladder ladder) {
        if (!invitee.hasDuelRequest(inviter)) {
            Messager.messageError(invitee, "You have not received a duel request from this player.");
            return;
        }

        acceptInvite0(inviter, invitee, ladder);
    }
}
