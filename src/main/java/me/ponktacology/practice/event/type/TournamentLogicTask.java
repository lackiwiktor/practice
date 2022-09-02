package me.ponktacology.practice.event.type;

import me.ponktacology.practice.Messages;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class TournamentLogicTask extends BukkitRunnable {

  private final Tournament tournament;

  @Setter private int nextAction = 1;

  @Override
  public void run() {
    nextAction--;

    switch (tournament.getState()) {
      case PLAYING:
        if (tournament.canStartNextRound()) {
          if (tournament.canEndTournament()) {
            tournament.setState(TournamentState.ENDING, 1);
            return;
          }
          tournament.setState(TournamentState.NEXT_ROUND_COUNTDOWN, 10);
        }
        break;
      case NEXT_ROUND_COUNTDOWN:
        if (nextAction == 0) {
          Bukkit.broadcastMessage(Messages.MATCH_START.get());
          tournament.onRoundStart();
          tournament.setState(TournamentState.PLAYING);
        } else {
          Bukkit.broadcastMessage(Messages.MATCH_COUNTDOWN.match("{time}", nextAction));
        }
        break;
      case START_COUNTODWN:
        if (!tournament.hasEnoughPlayers()) {
          tournament.setState(TournamentState.WAITING_FOR_TEAMS);
          Bukkit.broadcastMessage("Too few players!");
          return;
        }

        if (tournament.isFull()) {
          nextAction = 0;
        }

        if (nextAction == 0) {
          Bukkit.broadcastMessage(Messages.TOURNAMENT_START.get());
          tournament.onRoundStart();
          tournament.setState(TournamentState.PLAYING);
        } else {
          Bukkit.broadcastMessage(Messages.TOURNAMENT_COUNTDOWN.match("{time}", nextAction));
        }
        break;
      case WAITING_FOR_TEAMS:

        if (tournament.hasEnoughPlayers()) {
          tournament.setState(TournamentState.START_COUNTODWN, 20);
        }

        break;
      case ENDING:
        if (nextAction == 0) {
          tournament.onTournamentEnd();
        }
        break;
    }
  }
}
