package country.pvp.practice.match;

import country.pvp.practice.Expiring;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerData;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;

@Data
public class PlayerMatchData implements PlayerData {

    private final Match match;
    private final PlayerMatchStatistics statistics = new PlayerMatchStatistics();
    private final PearlCooldown pearlCooldown = new PearlCooldown();
    private boolean dead;
    private boolean disconnected;
    private PracticePlayer lastAttacker;

    public void handleHit() {
        statistics.handleHit();
    }

    public void handleBeingHit(PracticePlayer player) {
        statistics.handleBeingHit();
        lastAttacker = player;
    }

    public void increaseMissedPotions() {
        statistics.increaseMissedPotions();
    }

    public void increaseThrownPotions() {
        statistics.increasedThrownPotions();
    }

    public boolean hasPearlCooldownExpired() {
        return pearlCooldown.hasExpired();
    }

    public long getPearlCooldownRemaining() {
        return pearlCooldown.getRemaining();
    }

    public void resetPearlCooldown() {
        pearlCooldown.reset();
    }

    public void notifyAboutPearlCooldownExpiration(PracticePlayer player) {
        pearlCooldown.notify(player);
    }

    static class PearlCooldown implements Expiring {

        private long lastUsage;
        private boolean notify;

        @Override
        public boolean hasExpired() {
            return getPassed() > 16_000L;
        }

        public void reset() {
            lastUsage = System.currentTimeMillis();
            notify = true;
        }

        public long getRemaining() {
            return 16_000L - getPassed();
        }

        long getPassed() {
            return TimeUtil.elapsed(lastUsage);
        }

        public void notify(PracticePlayer player) {
            if (notify) {
                Messager.message(player, Messages.MATCH_PLAYER_PEARL_COOLDOWN_EXPIRED);
                notify = false;
            }
        }
    }
}
