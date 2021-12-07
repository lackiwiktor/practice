package country.pvp.practice.duel;

import country.pvp.practice.expiring.Expiring;
import country.pvp.practice.time.TimeUtil;

public abstract class Request implements Expiring {

   protected final long createdAt = System.currentTimeMillis();

    @Override
    public boolean hasExpired() {
        return TimeUtil.elapsed(createdAt) > 45_000L;
    }
}
