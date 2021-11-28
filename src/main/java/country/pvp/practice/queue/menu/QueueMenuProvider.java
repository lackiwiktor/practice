package country.pvp.practice.queue.menu;

import com.google.inject.Inject;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.QueueManager;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueMenuProvider {

    private final @NotNull QueueManager queueManager;
    private final @NotNull MatchManager matchManager;

    public @NotNull QueueMenu provide(boolean ranked, PracticePlayer player) {
        return new QueueMenu(queueManager, matchManager, ranked, player);
    }
}
