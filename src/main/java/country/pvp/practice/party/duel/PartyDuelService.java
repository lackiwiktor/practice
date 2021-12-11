package country.pvp.practice.party.duel;

import com.google.inject.Inject;
import country.pvp.practice.duel.DuelService;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.party.Party;

public class PartyDuelService extends DuelService<Party, PartyDuelRequest> {

    @Inject
    public PartyDuelService(InvitationService invitationService) {
        super(invitationService);
    }

    @Override
    protected void handleAccept(Party inviter, Party invitee, Ladder ladder) {

    }

    @Override
    protected boolean canSendDuel(Party inviter, Party invitee) {
        return false;
    }

    @Override
    protected boolean canAcceptDuel(Party inviter, Party invitee) {
        return false;
    }

    @Override
    protected PartyDuelRequest createDuelRequest(Party inviter, Ladder ladder) {
        return new PartyDuelRequest(inviter, ladder);
    }
}
