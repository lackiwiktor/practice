package country.pvp.practice.queue;

import com.google.inject.Inject;
import country.pvp.practice.team.SoloTeam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueMenuProvider {

    private final QueueManager queueManager;

    public QueueMenu provide(MatchType type, SoloTeam team) {
        return new QueueMenu(queueManager, type, team);
    }
}
