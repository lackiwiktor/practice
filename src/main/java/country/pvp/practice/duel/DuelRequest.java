package country.pvp.practice.duel;

import country.pvp.practice.ladder.Ladder;
import lombok.Data;

@Data
public class DuelRequest extends Request {
    private final Ladder ladder;
}
