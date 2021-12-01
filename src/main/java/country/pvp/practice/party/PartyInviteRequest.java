package country.pvp.practice.party;

import country.pvp.practice.duel.Request;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.time.TimeUtil;
import lombok.Data;

@Data
public class PartyInviteRequest extends Request {

    private final PracticePlayer invitee;

    @Override
    public boolean hasExpired() {
        return TimeUtil.elapsed(createdAt) > 120_000L;
    }
}
