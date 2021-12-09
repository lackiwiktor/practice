package country.pvp.practice.match.team;

import country.pvp.practice.party.Party;

public final class PartyTeam extends MultiTeam {

    private final Party party;

    private PartyTeam(Party party) {
        super(party.getMembers());
        this.party = party;
    }

    public static PartyTeam of(Party party) {
        return new PartyTeam(party);
    }

    @Override
    public String getName() {
        return party.getName();
    }
}
