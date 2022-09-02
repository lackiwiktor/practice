package me.ponktacology.practice.leaderboards;

import com.google.common.collect.Maps;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.player.PlayerRepository;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class LeaderBoardsService {

    private final Map<Ladder, List<PracticePlayer>> cachedTopPlayers = Maps.newConcurrentMap();
    private final PlayerRepository playerRepository;

    public List<PracticePlayer> getLeaderBoardPlayers(Ladder ladder) {
        return cachedTopPlayers.getOrDefault(ladder, Collections.emptyList());
    }

    void fetchTopPlayers(Ladder ladder) {
        final String fieldName = "statistics.".concat(ladder.getName());
        List<PracticePlayer> sessions = playerRepository.get(Filters.exists(fieldName), Sorts.descending(fieldName), 5);

        playerRepository.createIndex(Indexes.descending(fieldName));
        cachedTopPlayers.put(ladder, sessions);
    }
}
