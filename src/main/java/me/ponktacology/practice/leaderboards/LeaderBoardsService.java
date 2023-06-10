package me.ponktacology.practice.leaderboards;

import com.google.common.collect.Maps;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderService;
import me.ponktacology.practice.leaderboards.command.LeaderBoardsCommand;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class LeaderBoardsService extends Service {

  private final Map<Ladder, List<PracticePlayer>> cachedTopPlayers = Maps.newConcurrentMap();

  @Override
  protected void configure() {
    registerTask(
        () -> {
          for (Ladder ladder : Practice.getService(LadderService.class).getAllLadders()) {
            fetchTopPlayers(ladder);
          }
        },
        20L,
        TimeUnit.SECONDS,
        true);
    addCommand(new LeaderBoardsCommand());
  }

  public List<PracticePlayer> getLeaderBoardPlayers(Ladder ladder) {
    return cachedTopPlayers.getOrDefault(ladder, Collections.emptyList());
  }

  private void fetchTopPlayers(Ladder ladder) {
    final String fieldName = "statistics.".concat(ladder.getName()).concat(".mean");
    PlayerService playerService = Practice.getService(PlayerService.class);
    List<PracticePlayer> players = playerService.get(Filters.exists("_id"), Sorts.descending(fieldName), 10);
    playerService.createIndex(Indexes.descending(fieldName));
    cachedTopPlayers.put(ladder, players);
  }
}
