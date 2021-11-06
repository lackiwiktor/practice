package country.pvp.practice.queue;

import country.pvp.practice.team.PlayerTeam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueMenuProvider {

    private final QueueManager queueManager;

    public QueueMenu provide(MatchType type, PlayerTeam team) {
        return new QueueMenu(queueManager, type, team);
    }
}
