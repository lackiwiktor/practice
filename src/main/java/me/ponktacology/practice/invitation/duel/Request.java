package me.ponktacology.practice.invitation.duel;

import me.ponktacology.practice.util.Expiring;
import me.ponktacology.practice.util.TimeUtil;

public abstract class Request implements Expiring {

   protected final long createdAt = System.currentTimeMillis();

    @Override
    public boolean hasExpired() {
        return TimeUtil.elapsed(createdAt) > 45_000L;
    }
}
