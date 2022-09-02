package me.ponktacology.practice.match.info;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class PlayerInfoTracker {

  private final Match match;
  private final Map<PracticePlayer, PlayerMatchInfo> matchInfo = Maps.newHashMap();

  public boolean isAlive(PracticePlayer player) {
    return !getInfo(player).isDead();
  }

  public void setDead(PracticePlayer player, boolean dead) {
    getInfo(player).setDead(dead);
  }

  public boolean isDisconnected(PracticePlayer player) {
    return getInfo(player).isDisconnected();
  }

  public void setDisconnected(PracticePlayer disconnectedPlayer, boolean disconnected) {
    getInfo(disconnectedPlayer).setDisconnected(disconnected);
  }

  public boolean hasLastAttacker(PracticePlayer player) {
    return getInfo(player).getLastAttacker() != null;
  }

  public PracticePlayer getLastAttacker(PracticePlayer player) {
    return getInfo(player).getLastAttacker();
  }

  private PlayerMatchInfo getInfo(PracticePlayer player) {
    Preconditions.checkArgument(match.isInMatch(player), "player is not in this match");

    return matchInfo.computeIfAbsent(player, key -> new PlayerMatchInfo());
  }

  public void setLastAttacker(PracticePlayer damagedPlayer, PracticePlayer damagerPlayer) {
    getInfo(damagedPlayer).setLastAttacker(damagerPlayer);
  }
}
