package country.pvp.practice.duel;

import country.pvp.practice.Expiring;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;

@Data
public class DuelRequest implements Expiring {

    private final PracticePlayer player;
    private final Ladder ladder;
    private final long timeStamp = System.currentTimeMillis();

    @Override
    public boolean hasExpired() {
        return TimeUtil.elapsed(timeStamp) > 30_000L;
    }
}
