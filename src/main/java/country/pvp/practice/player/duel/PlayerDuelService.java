package country.pvp.practice.player.duel;

import com.google.inject.Inject;
import country.pvp.practice.duel.DuelService;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.Sender;

public class PlayerDuelService extends DuelService<PlayerSession, PlayerDuelRequest> {

    private final MatchProvider matchProvider;

    @Inject
    public PlayerDuelService(InvitationService invitationService, MatchProvider matchProvider) {
        super(invitationService);
        this.matchProvider = matchProvider;
    }

    @Override
    protected void handleAccept(PlayerSession inviter, PlayerSession invitee, Ladder ladder) {
        matchProvider.provide(ladder, false, true, SoloTeam.of(inviter), SoloTeam.of(invitee)).init();
    }

    @Override
    protected boolean canSendDuel(PlayerSession inviter, PlayerSession invitee) {
        if (invitee.hasDuelRequest(inviter)) {
            Sender.messageError(inviter, "You already invited this player for a duel.");
            return false;
        }

        return check(inviter, invitee);
    }

    @Override
    protected boolean canAcceptDuel(PlayerSession inviter, PlayerSession invitee) {
        return check(inviter, invitee);
    }

    private boolean check(PlayerSession inviter, PlayerSession invitee) {
        if (inviter.hasParty()) {
            Sender.messageError(invitee, "Player can't be in a party if you want to duel him.");
            return false;
        }

        if (invitee.hasParty()) {
            Sender.messageError(invitee, "You can't be in a party if you want to duel someone.");
            return false;
        }

        if (!invitee.isInLobby()) {
            Sender.messageError(invitee, "You must be in lobby if you want to duel someone.");
            return false;
        }

        if (!inviter.isInLobby()) {
            Sender.messageError(invitee, "This player is busy right now.");
            return false;
        }

        return true;
    }

    @Override
    protected PlayerDuelRequest createDuelRequest(PlayerSession inviter, Ladder ladder) {
        return new PlayerDuelRequest(inviter, ladder);
    }
}
