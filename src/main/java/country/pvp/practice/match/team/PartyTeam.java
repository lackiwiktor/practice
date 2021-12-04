package country.pvp.practice.match.team;

import com.google.common.collect.ImmutableList;
import country.pvp.practice.party.Party;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PartyTeam extends Team {

    private final Party party;

    public static PartyTeam of(Party party) {
        return new PartyTeam(party);
    }

    @Override
    public String getName() {
        return party.getName();
    }

    @Override
    public List<PlayerSession> getPlayers() {
        return ImmutableList.copyOf(party.getMembers());
    }

    @Override
    public int getPing() {
        return -1;
    }


}
