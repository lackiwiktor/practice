package me.ponktacology.practice.match;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.event.MatchStartCountdownEvent;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.util.EventUtil;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

@RequiredArgsConstructor
class MatchLogicTask extends BukkitRunnable {

  private final Match match;

  @Setter private int nextAction = 1;

  @Override
  public void run() {
    nextAction--;

    switch (match.getState()) {
      case STARTING:
        if (nextAction == 0) {
          match.onMatchStart();
          EventUtil.callEvent(new MatchStartCountdownEvent(match));
          match.setState(MatchState.COUNTDOWN, 6);
        }
        break;
      case COUNTDOWN:
        if (nextAction == 0) {
          Messenger.message(match, Messages.MATCH_START.get());
          match.setState(MatchState.IN_PROGRESS);
          EventUtil.callEvent(new MatchStartEvent(match));
        } else {
          Messenger.message(match, Messages.MATCH_COUNTDOWN.match("{time}", nextAction));
        }
        break;
      case IN_PROGRESS:
        if (nextAction < -60) {
          match.cancel("Match time exceeded.");
        }
        break;
      case ENDING:
        if (nextAction == 0) {
          match.onRoundEnd();
          match.setState(MatchState.FINISHED, 4);
        }
        break;
      case FINISHED:
        System.out.println(
            "Finished? "
                + nextAction
                + match.getTeams().stream()
                    .map(it -> it.getName())
                    .collect(Collectors.joining(", ")));
        if (nextAction <= 0) {
          match.onMatchEnd();
          cancel();
        }
        break;
    }
  }
}
