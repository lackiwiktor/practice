package country.pvp.practice.party.menu;

import com.google.inject.Inject;
import country.pvp.practice.party.Party;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyMembersMenuProvider {

    public PartyMembersMenu provide(Party party) {
        return new PartyMembersMenu(party);
    }
}
