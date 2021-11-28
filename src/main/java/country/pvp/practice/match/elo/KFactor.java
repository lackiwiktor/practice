package country.pvp.practice.match.elo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KFactor {

    private final int startIndex;
    private final int endIndex;
    private final double value;

}
