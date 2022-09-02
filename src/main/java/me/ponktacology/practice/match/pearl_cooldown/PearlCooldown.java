package me.ponktacology.practice.match.pearl_cooldown;

import me.ponktacology.practice.Messages;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.Expiring;
import me.ponktacology.practice.util.TimeUtil;
import me.ponktacology.practice.util.message.Messenger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PearlCooldown implements Expiring {

    private final PracticePlayer player;

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
            Messenger.message(player, Messages.MATCH_PLAYER_PEARL_COOLDOWN_EXPIRED);
            notify = false;
        }
    }
}
