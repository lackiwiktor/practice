package me.ponktacology.practice.match.statistics;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class PlayerStatisticsTracker {

  private final Match match;
  private final Map<PracticePlayer, PlayerMatchStatistics> statistics = Maps.newHashMap();

  public void onPlayerAttack(PracticePlayer damagerPlayer) {
    PlayerMatchStatistics statistics = getStatistics(damagerPlayer);
    statistics.onPlayerAttack();
  }

  public void onPlayerBeingAttacked(PracticePlayer damagedPlayer) {
    PlayerMatchStatistics statistics = getStatistics(damagedPlayer);
    statistics.onPlayerBeingAttacked();
  }

  public void increaseThrownPots(PracticePlayer player) {
    PlayerMatchStatistics statistics = getStatistics(player);
    statistics.increaseThrownPotions();
  }

  public void increaseMissedPots(PracticePlayer player) {
    PlayerMatchStatistics statistics = getStatistics(player);
    statistics.increaseMissedPotions();
  }

  public void clear() {
    statistics.clear();
  }

  public PlayerMatchStatistics getStatistics(PracticePlayer player) {
    Preconditions.checkArgument(match.isInMatch(player), "player is not in this match");

    return statistics.computeIfAbsent(player, key -> new PlayerMatchStatistics());
  }

  public int getHits(PracticePlayer damagerPlayer) {
    return getStatistics(damagerPlayer).getHits();
  }
}
