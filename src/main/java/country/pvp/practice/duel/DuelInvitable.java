package country.pvp.practice.duel;

import country.pvp.practice.invitation.Invitable;
import org.jetbrains.annotations.Nullable;

public interface DuelInvitable<V, D extends DuelRequest<V>> extends Invitable {

    void addDuelRequest(D request);

    boolean hasDuelRequest(DuelInvitable inviter);

    void clearDuelRequests(DuelInvitable inviter);

    void invalidateDuelRequests();

    @Nullable D getDuelRequest(DuelInvitable inviter);


}
