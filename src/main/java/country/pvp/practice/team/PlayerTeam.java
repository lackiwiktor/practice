package country.pvp.practice.team;

import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.queue.QueueData;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class PlayerTeam extends QueueableTeam {

    private final PracticePlayer player;

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Set<PracticePlayer> getPlayers() {
        return Collections.singleton(player);
    }

    @Override
    public void startQueuing(QueueData<?> queueData) {
        player.startQueuing(queueData);
    }

    @Override
    public void stopQueueing() {
        player.removeFromQueue();
    }
}
