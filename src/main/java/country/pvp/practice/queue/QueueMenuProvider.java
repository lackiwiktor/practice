package country.pvp.practice.queue;

import com.google.inject.Inject;
import country.pvp.practice.team.PlayerTeam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class QueueMenuProvider {

    private final QueueManager queueManager;

    public QueueMenu provide(MatchType type, PlayerTeam team) {
        return new QueueMenu(queueManager, type, team);
    }
}
