package country.pvp.practice.match;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchMenuProvider {

    private final MatchManager matchManager;

    public MatchMenu provide() {
        return new MatchMenu(matchManager);
    }
}
