package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.util.TaskDispatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class MatchDurationLimitListener implements Listener {

    @EventHandler
    public void onMatchStart(MatchStartEvent event) {
    TaskDispatcher.runLater(
        () -> {
          Match match = event.getMatch();
          if (match.getState() == MatchState.IN_PROGRESS) {
            match.cancel("Match duration exceeded.");
          }
        },
        5L,
        TimeUnit.MINUTES);
    }
}
