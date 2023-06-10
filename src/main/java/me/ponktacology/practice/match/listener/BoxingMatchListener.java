package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.event.MatchPlayerDamageByPlayerEvent;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import org.bukkit.event.EventHandler;

public class BoxingMatchListener extends PracticePlayerListener {

  private static final int HITS_NEEDED_TO_WIN = 10;

  @EventHandler(ignoreCancelled = true)
  public void playerDamageEvent(MatchPlayerDamageByPlayerEvent event) {
    Match match = event.getMatch();

    if (!match.getLadder().isBoxing()) {
      return;
    }

    event.getBukkitEvent().setDamage(0);

    PracticePlayer damagerPlayer = event.getDamager();
    PlayerStatisticsTracker statisticsTracker = match.getStatisticsTracker();
    if (statisticsTracker.getHits(damagerPlayer) >= HITS_NEEDED_TO_WIN) {
      match.endMatch(match.getTeam(damagerPlayer));
    }
  }
}
