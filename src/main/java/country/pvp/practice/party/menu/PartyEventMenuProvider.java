package country.pvp.practice.party.menu;

import com.google.inject.Inject;
import country.pvp.practice.kit.editor.KitChooseMenuProvider;
import country.pvp.practice.match.MatchProvider;
import country.pvp.practice.party.Party;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyEventMenuProvider {

    private final MatchProvider matchProvider;
    private final KitChooseMenuProvider kitChooseMenuProvider;

    public PartyEventMenu provide(Party party) {
        return new PartyEventMenu(matchProvider, kitChooseMenuProvider, party);
    }
}
