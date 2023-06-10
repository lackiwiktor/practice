package me.ponktacology.practice.match.team;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.Getter;
import me.ponktacology.practice.match.participant.GameParticipant;
import me.ponktacology.practice.util.message.Recipient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Team implements Recipient {

  @Getter protected final TeamMatchStatistics statistics = new TeamMatchStatistics();
  protected final Map<UUID, GameParticipant> players = Maps.newHashMap();

  public abstract String getName();

  public int size() {
    return players.size();
  }

  public boolean hasPlayer(UUID uuid) {
    return players.containsKey(uuid);
  }

  public boolean hasPlayer(GameParticipant participant) {
    return players.containsKey(participant.getUuid());
  }

  @Override
  public void receive(String message) {
    /*
    for (PracticePlayer player : players) {
      if(!player.isOnline()) continue;
      player.receive(message);
    }
     */
  }

  public List<GameParticipant> getPlayers() {
    return ImmutableList.copyOf(players.values());
  }

  public List<GameParticipant> getOnlinePlayers() {
    return players.values().stream().filter(GameParticipant::isOnline).collect(Collectors.toList());
  }

  public boolean isDead() {
    return getPlayers().stream().allMatch(GameParticipant::isDead);
  }

  public int getAlivePlayersCount() {
    return (int) getPlayers().stream().filter(it -> it.isAlive()).count();
  }
}
