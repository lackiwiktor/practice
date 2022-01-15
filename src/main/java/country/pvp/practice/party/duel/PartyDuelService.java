package country.pvp.practice.party.duel;

import com.google.inject.Inject;
import country.pvp.practice.duel.DuelService;
import country.pvp.practice.invitation.InvitationService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.match.team.type.PartyTeam;
import country.pvp.practice.party.Party;

public class PartyDuelService extends DuelService<Party, PartyDuelRequest> {

    private final MatchProvider matchProvider;

    @Inject
    public PartyDuelService(InvitationService invitationService, MatchProvider matchProvider) {
        super(invitationService);
        this.matchProvider = matchProvider;
    }

    @Override
    protected void handleAccept(Party inviter, Party invitee, Ladder ladder) {
        matchProvider.provide(ladder, false, true, PartyTeam.of(inviter), PartyTeam.of(invitee));
    }

    @Override
    protected boolean canSendDuel(Party inviter, Party invitee) {
        return true;
    }

    @Override
    protected boolean canAcceptDuel(Party inviter, Party invitee) {
        return true;
    }

    @Override
    protected PartyDuelRequest createDuelRequest(Party inviter, Ladder ladder) {
        return new PartyDuelRequest(inviter, ladder);
    }
}
