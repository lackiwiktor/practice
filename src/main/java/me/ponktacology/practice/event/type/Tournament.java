package me.ponktacology.practice.event.type;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.event.Event;
import me.ponktacology.practice.event.EventType;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.team.type.PartyTeam;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.util.TaskDispatcher;
import me.ponktacology.practice.util.message.Messenger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class Tournament extends Event<Party> {

  private final Ladder ladder;
  private final int teamSize;
  private final int maxParties;
  private final int requiredParties;
  private final Set<Match> activeMatches = Sets.newHashSet();
  private final Map<Team, Party> teamToParty = Maps.newHashMap();
  private final TournamentLogicTask logicTask = new TournamentLogicTask(this);
  private int currentRound;
  @Setter private TournamentState state; // late init
  private int initialSize;

  public Tournament(Ladder ladder, int teamSize, int maxParties, int requiredParties) {
    super(EventType.TOURNAMENT, false);
    this.ladder = ladder;
    this.teamSize = teamSize;
    this.maxParties = maxParties;
    this.requiredParties = requiredParties;
  }

  @Override
  public void init() {
    setState(TournamentState.WAITING_FOR_TEAMS, 1);
    TaskDispatcher.scheduleSync(logicTask, 1L, TimeUnit.SECONDS);
  }

  @Override
  protected boolean canJoin(Party party) {
    if (state != TournamentState.WAITING_FOR_TEAMS && state != TournamentState.START_COUNTODWN) {
      Messenger.messageError(party, Messages.TOURNAMENT_ALREADY_STARTED.get());
      return false;
    }

    if (participants.size() > maxParties) {
      Messenger.messageError(party, Messages.TOURNAMENT_FULL.get());
      return false;
    }

    if (party.size() != teamSize) {
      Messenger.messageError(
          party, Messages.TOURNAMENT_INVALID_PARTY_SIZE.match("{size}", teamSize));
      return false;
    }

    return true;
  }

  boolean canStartNextRound() {
    removeLosers();
    return activeMatches.stream().allMatch(Match::isFinished);
  }

  void onRoundStart() {
    if (currentRound == 0) {
      initialSize = getActiveParties().size();
    }
    currentRound++;
    removeLosers();
    startMatches();
  }

  void onTournamentEnd() {
    finish();
    if (getActiveParties().size() == 0) {
      Bukkit.broadcastMessage("No winner!");
      return;
    }

    Party winner = getActiveParties().get(0);
    Bukkit.broadcastMessage("Winner: " + winner.getName());
  }

  @Override
  protected void cancel() {
    activeMatches.forEach(it -> it.cancel("Tournament is cancelled."));
    activeMatches.clear();
    getActiveParties().clear();
  }

  private void startMatches() {
    activeMatches.clear();

    List<Party> partyList = new ArrayList<>(getActiveParties());

    while (partyList.size() > 1) {
      Party party1 = partyList.remove(0);
      Party party2 = partyList.remove(0);

      Match match =
          Practice.getService(MatchService.class)
              .start(ladder,  null,false, false, PartyTeam.of(party1), PartyTeam.of(party2));

      activeMatches.add(match);
    }

    if (partyList.size() == 1) {
      Party party = partyList.get(0);
      Messenger.message(party, Messages.TOURNAMENT_ODD_PARTY_COUNT);
    }

    mapPartiesToTeams();
  }

  private void removeLosers() {
    // Removing disbanded parties
    participants.removeIf(it -> it.isDisbanded());
    Iterator<Party> partyIterator = participants.iterator();

    while (partyIterator.hasNext()) {
      if (partyIterator.next().isDisbanded()) {
        partyIterator.remove();
      }
    }

    Iterator<Match> matchIterator = activeMatches.iterator();

    while (matchIterator.hasNext()) {
      Match match = matchIterator.next();

      if (match.getState() != MatchState.FINISHED) {
        continue;
      }

      for (Team loser : match.getLosers()) {
        Party loserParty = teamToParty(loser);

        if (loserParty == null) {
          new IllegalStateException("party is invalid??").printStackTrace();
          continue;
        }

        participants.remove(loserParty);
      }

      matchIterator.remove();
    }
  }

  public boolean canEndTournament() {
    return getActiveParties().size() <= 1;
  }

  void setState(TournamentState state, int delay) {
    this.state = state;
    logicTask.setNextAction(delay);
  }

  private void mapPartiesToTeams() {
    for (Match matches : activeMatches) {
      for (Team team : matches.getTeams()) {
        if (!(team instanceof PartyTeam))
          throw new IllegalStateException("all teams should be party teams");

        PartyTeam partyTeam = (PartyTeam) team;
        teamToParty.put(team, partyTeam.getParty());
      }
    }
  }

  private Party teamToParty(Team team) {
    return teamToParty.get(team);
  }

  boolean hasEnoughPlayers() {
    return getActiveParties().size() >= requiredParties;
  }

  boolean isFull() {
    return getActiveParties().size() == maxParties;
  }

  private List<Party> getActiveParties() {
    return participants.stream().filter(it -> !it.isDisbanded()).collect(Collectors.toList());
  }

  public int getInitialSize() {
    return initialSize;
  }
}
