package me.ponktacology.practice.match;

import com.google.common.collect.Sets;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.arena.ArenaService;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.match.command.MatchCommands;
import me.ponktacology.practice.match.listener.*;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.match.type.FreeForAllMatch;
import me.ponktacology.practice.match.type.TeamMatch;
import me.ponktacology.practice.util.message.Messenger;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class MatchService extends Service {

  private final Set<Match> matches = Sets.newConcurrentHashSet();

  @Override
  public void configure() {
    addListener(new BasicMatchListener());
    addListener(new BoxingMatchListener());
    addListener(new MatchBuildListener());
    addListener(new SumoMatchListener());
    addListener(new MatchKitListener());
    addCommand(new MatchCommands(this));
  }

  @Override
  public void stop() {
    super.stop();

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

  public int getPlayersInFightCount(Ladder ladder, boolean ranked) {
    return matches.stream()
        .filter(it -> !it.isDuel() && it.getLadder().equals(ladder) && it.isRanked() == ranked)
        .mapToInt(Match::getPlayersCount)
        .sum();
  }

  public int getPlayersInFightCount() {
    return matches.stream().filter(it -> !it.isDuel()).mapToInt(Match::getPlayersCount).sum();
  }

  public @Nullable Match start(Ladder ladder, boolean ranked, boolean duel, Team teamA, Team teamB) {
    TeamMatch match = createMatch(ladder, ranked, duel, teamA, teamB);

    if (match == null) {
      return null;
    }

    match.init();

    return match;
  }

  public void start(Ladder ladder, SoloTeam... teams) {
    FreeForAllMatch match = createMatch(ladder, teams);

    if (match == null) {
      return;
    }

    match.init();
  }

  private @Nullable TeamMatch createMatch(
      Ladder ladder, boolean ranked, boolean duel, Team teamA, Team teamB) {
    MatchArenaCopy arena = Practice.getService(ArenaService.class).getRandomMatchArena();

    if (arena == null) {
      Messenger.messageError(teamA, "No arenas are available right now.");
      Messenger.messageError(teamB, "No arenas are available right now.");

      LobbyService lobbyService = Practice.getService(LobbyService.class);
      teamA.getOnlinePlayers().forEach(player -> lobbyService.moveToLobby(player));
      teamB.getOnlinePlayers().forEach(player -> lobbyService.moveToLobby(player));
      return null;
    }

    return new TeamMatch(ladder, ranked, duel, arena, teamA, teamB);
  }

  private @Nullable FreeForAllMatch createMatch(Ladder ladder, Team... teams) {
    MatchArenaCopy arena = Practice.getService(ArenaService.class).getRandomMatchArena();

    if (arena == null) {
      for (Team team : teams) {
        Messenger.messageError(team, "No arenas are available right now.");

        team.getOnlinePlayers().forEach(Practice.getService(HotBarService.class)::apply);
      }
      return null;
    }

    return new FreeForAllMatch(ladder, false, false, arena, teams);
  }
}
