package me.ponktacology.practice.match.team.type;

import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.party.Party;
import lombok.Data;
import lombok.Getter;

@Data(staticConstructor = "of")
public class PartyTeam extends Team {

    @Getter
    private final Party party;

    private PartyTeam(Party party) {
        super();
        this.party = party;
        this.players.addAll(party.getMembers());
    }

    @Override
    public String getName() {
        return party.getName();
    }
}
