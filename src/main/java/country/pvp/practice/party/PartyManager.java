package country.pvp.practice.party;

import com.google.common.collect.Sets;

import java.util.Collections;
import java.util.Set;

public class PartyManager {

    private final Set<Party> parties = Sets.newHashSet();

    public void add(Party party) {
        parties.add(party);
    }

    public void remove(Party party) {
        parties.remove(party);
    }

    public Set<Party> getAll() {
        return Collections.unmodifiableSet(parties);
    }
}
