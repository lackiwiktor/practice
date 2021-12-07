package country.pvp.practice.leaderboards;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.player.PlayerService;
import country.pvp.practice.player.PlayerSession;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class LeaderBoardsService {

    private final Map<Ladder, List<PlayerSession>> cachedTopPlayers = Maps.newConcurrentMap();
    private final PlayerService playerService;

    public List<PlayerSession> getLeaderBoardPlayers(Ladder ladder) {
        return cachedTopPlayers.getOrDefault(ladder, Collections.emptyList());
    }

    void fetchTopPlayers(Ladder ladder) {
        final String fieldName = "statistics.".concat(ladder.getName());
        List<PlayerSession> sessions = playerService.get(Filters.exists(fieldName), Sorts.descending(fieldName), 5);

        if (!sessions.isEmpty()) //work-around
            playerService.createIndex(sessions.get(0), Indexes.descending(fieldName));
        
        cachedTopPlayers.put(ladder, sessions);
    }
}
