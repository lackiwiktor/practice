package me.ponktacology.practice.event.type;

import me.ponktacology.practice.Messages;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ThimbleLogicTask extends BukkitRunnable {

  private final Thimble tournament;

  @Setter private int nextAction = 1;

  @Override
  public void run() {
    nextAction--;

    switch (tournament.getState()) {
      case START_COUNTODWN:
        if (!tournament.hasEnoughPlayers()) {
          tournament.setState(ThimbleState.WAITING_FOR_PLAYERS);
          Bukkit.broadcastMessage("Too few players!");
          return;
        }

        if (tournament.isFull()) {
          nextAction = 0;
        }

        if (nextAction == 0) {
          Bukkit.broadcastMessage(Messages.TOURNAMENT_START.get());
          tournament.startNextRound();
        } else {
          Bukkit.broadcastMessage(Messages.TOURNAMENT_COUNTDOWN.match("{time}", nextAction));
        }


        break;
      case CHOOSING_NEXT_JUMPER:
        tournament.pickNextJumper();
        break;
      case WAITING_FOR_PLAYERS:
        if (tournament.hasEnoughPlayers()) {
          tournament.setState(ThimbleState.START_COUNTODWN, 5);
        }

        break;
      case ENDING:
        break;
    }
  }
}
