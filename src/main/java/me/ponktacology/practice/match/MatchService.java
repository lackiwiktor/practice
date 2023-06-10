package me.ponktacology.practice.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.ponktacology.practice.Cache;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.arena.ArenaService;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.match.command.MatchCommands;
import me.ponktacology.practice.match.listener.*;
import me.ponktacology.practice.match.pearl_cooldown.PearlCooldownTask;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.match.type.FreeForAllMatch;
import me.ponktacology.practice.match.type.TeamMatch;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MatchService extends Service implements Cache {

  private final Set<Match> matches = Sets.newConcurrentHashSet();
  private final Map<UUID, Match> playerToMatchMap = Maps.newHashMap();

  @Override
  public void configure() {
    addListener(new MatchGeneralListener(this));
    addListener(new MatchBuildListener(this));
    addListener(new MatchKitListener(this));
    addListener(new MatchDurationLimitListener());
    addListener(new MatchEndingMessageListener());
    addListener(new BoxingMatchListener());
    addListener(new BridgeMatchListener(this));
    addListener(new ComboMatchListener());
    addListener(new SumoMatchListener());
    addCommand(new MatchCommands(this));
    registerTask(new PearlCooldownTask(), 250L, TimeUnit.SECONDS, false);
    registerTask(new BridgeTeleportTask(), 1L, TimeUnit.SECONDS, false);
    registerTask(new SumoCheckPlayerInWater(), 1L, TimeUnit.SECONDS, false);
  }

  @Override
  public void stop() {
    for (Match match : matches) {
      match.cancel("Server is restarting...");
    }
  }

  public void add(Match match) {
    matches.add(match);
  }

  public void remove(Match match) {
    matches.remove(match);
  }

  public Set<Match> getAll() {
    return Collections.unmodifiableSet(matches);
  }

  public void updatePlayerMatch(UUID player, @Nullable Match match) {
    if (match == null) {
      playerToMatchMap.remove(player);
      return;
    }

    playerToMatchMap.put(player, match);
  }

  public boolean isInMatch(PracticePlayer player) {
    return playerToMatchMap.containsKey(player.getUuid());
  }

  public Match getPlayerMatch(PracticePlayer player) {
    Preconditions.checkArgument(isInMatch(player), "player is not in a match");
    return playerToMatchMap.get(player);
  }

  public int getPlayersInFightCount(Ladder ladder, boolean ranked) {
    return matches.stream()
        .filter(it -> !it.isDuel() && it.getLadder().equals(ladder) && it.isRanked() == ranked)
        .mapToInt(Match::getPlayersCount)
        .sum();
  }

  public int getPlayersInFightCount() {
    return matches.stream().filter(it -> !it.isDuel()).mapToInt(Match::getPlayersCount).sum();
  }

  public @Nullable Match start(
      Ladder ladder, MatchArena arena, boolean ranked, boolean duel, Team teamA, Team teamB) {
    TeamMatch match = createMatch(ladder, arena, ranked, duel, teamA, teamB);

    if (match == null) {
      return null;
    }

    match.start();

    return match;
  }

  public void start(Ladder ladder, @Nullable MatchArena arena, SoloTeam... teams) {
    FreeForAllMatch match = createMatch(ladder, arena, teams);

    if (match == null) {
      return;
    }

    match.start();
  }

  private @Nullable TeamMatch createMatch(
      Ladder ladder,
      @Nullable MatchArena arena,
      boolean ranked,
      boolean duel,
      Team teamA,
      Team teamB) {
    MatchArenaCopy matchArena =
        arena == null
            ? Practice.getService(ArenaService.class).getRandomMatchArena()
            : arena.getAvailableCopy();

    if (matchArena == null) {
      Messenger.messageError(teamA, "No arenas are available right now.");
      Messenger.messageError(teamB, "No arenas are available right now.");

      LobbyService lobbyService = Practice.getService(LobbyService.class);
      teamA.getPlayers().stream().filter(it -> it.isOnline()).forEach(lobbyService::moveToLobby);
      teamB.getPlayers().stream().filter(it -> it.isOnline()).forEach(lobbyService::moveToLobby);
      return null;
    }

    return new TeamMatch(ladder, ranked, duel, matchArena, teamA, teamB);
  }

  private @Nullable FreeForAllMatch createMatch(
      Ladder ladder, @Nullable MatchArena arena, Team... teams) {
    MatchArenaCopy matchArena =
        arena == null
            ? Practice.getService(ArenaService.class).getRandomMatchArena()
            : arena.getAvailableCopy();

    if (matchArena == null) {
      for (Team team : teams) {
        Messenger.messageError(team, "No arenas are available right now.");

        team.getPlayers().stream()
            .filter(it -> it.isOnline())
            .forEach(Practice.getService(HotBarService.class)::apply);
      }
      return null;
    }

    return new FreeForAllMatch(ladder, false, false, matchArena, teams);
  }

  @Override
  public int getSize() {
    return playerToMatchMap.size();
  }
}
