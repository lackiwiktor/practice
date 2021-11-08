package country.pvp.practice.queue;

import com.google.inject.Inject;
import country.pvp.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueMenuProvider {

    private final QueueManager queueManager;

    public QueueMenu provide(boolean ranked, PracticePlayer player) {
        return new QueueMenu(queueManager, ranked, player);
    }
}
