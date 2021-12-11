package country.pvp.practice.duel;

import country.pvp.practice.ladder.Ladder;
import lombok.Data;

@Data
public class DuelRequest<V> extends Request {

    private final V inviter;
    private final Ladder ladder;

}
