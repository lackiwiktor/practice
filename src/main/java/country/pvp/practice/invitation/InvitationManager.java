package country.pvp.practice.invitation;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InvitationManager {

    private final Map<UUID, Invitation> invitations = Maps.newConcurrentMap();

    public void add(Invitation invitation) {
        invitations.put(invitation.getId(), invitation);
    }

    public Optional<Invitation> get(UUID uuid) {
        return Optional.ofNullable(invitations.get(uuid));
    }

    public void remove(Invitation invitation) {
        invitations.remove(invitation.getId());
    }

    public void invalidate() {
        invitations.entrySet().removeIf(entry -> entry.getValue().hasExpired());
    }
}
