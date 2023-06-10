package me.ponktacology.practice.match.pearl_cooldown;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.util.Expiring;
import me.ponktacology.practice.util.TimeUtil;

@RequiredArgsConstructor
public class PearlCooldown implements Expiring {

    private long lastUsage;
    private boolean notify;

    @Override
    public boolean hasExpired() {
        return getPassed() >= 16_000L;
    }

    public void reset() {
        lastUsage = System.currentTimeMillis();
        notify = true;
    }

    public long getRemaining() {
        return 16_000L - getPassed();
    }

    private long getPassed() {
        return TimeUtil.elapsed(lastUsage);
    }

    public void notifyPlayer() {
        if (notify) {
            // TODO: Move this to different place
          //  Messenger.message(player, Messages.MATCH_PLAYER_PEARL_COOLDOWN_EXPIRED);
            notify = false;
        }
    }
}
