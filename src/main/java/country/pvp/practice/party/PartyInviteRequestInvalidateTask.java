package country.pvp.practice.party;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class PartyInviteRequestInvalidateTask implements Runnable {

    private final PartyManager partyManager;

    @Override
    public void run() {
        for (Party party : partyManager.getAll()) {
            party.invalidateInviteRequests();
        }
    }
}
