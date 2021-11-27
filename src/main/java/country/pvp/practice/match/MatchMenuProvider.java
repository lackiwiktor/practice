package country.pvp.practice.match;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchMenuProvider {

    private final MatchManager matchManager;

    public MatchMenu provide() {
        return new MatchMenu(matchManager);
    }
}
