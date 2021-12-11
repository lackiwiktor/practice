package country.pvp.practice.player.duel;

import com.google.inject.Inject;
import country.pvp.practice.duel.DuelService;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.message.Messager;
import country.pvp.practice.player.PlayerSession;

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
        if (!inviter.isInLobby()) {
            Messager.messageError(inviter, "You must be in lobby in order to duel someone.");
            return false;
        }

        if (!invitee.isInLobby()) {
            Messager.messageError(inviter, "This player is busy right now.");
            return false;
        }

        if (invitee.hasDuelRequest(inviter)) {
            Messager.messageError(inviter, "You already invited this player for a duel.");
            return false;
        }

        return true;
    }

    @Override
    protected boolean canAcceptDuel(PlayerSession inviter, PlayerSession invitee) {
        if (!invitee.isInLobby()) {
            Messager.messageError(invitee, "You must be in lobby in order to duel someone.");
            return false;
        }

        if (!inviter.isInLobby()) {
            Messager.messageError(invitee, "This player is busy right now.");
            return false;
        }

        return true;
    }

    @Override
    protected PlayerDuelRequest createDuelRequest(PlayerSession inviter, Ladder ladder) {
        return new PlayerDuelRequest(inviter, ladder);
    }
}
