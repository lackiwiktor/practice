package me.ponktacology.practice.invitation;

import me.ponktacology.practice.util.Expiring;
import me.ponktacology.practice.util.TimeUtil;
import lombok.Data;

import java.util.UUID;

@Data
public abstract class Invitation<V extends Invitable> implements Expiring {

    private final UUID id = UUID.randomUUID();
    private final long timeStamp = System.currentTimeMillis();
    private final String message;
    private final V inviter;

    public boolean accept() {
        return onAccept();
    }

    public void decline() {
        onDecline();
    }

    protected abstract boolean onAccept();

    protected abstract void onDecline();

    @Override
    public boolean hasExpired() {
        return TimeUtil.elapsed(timeStamp) > 15_000L;
    }
}
