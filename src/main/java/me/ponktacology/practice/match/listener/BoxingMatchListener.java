package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.ladder.LadderType;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.statistics.PlayerStatisticsTracker;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BoxingMatchListener extends PracticePlayerListener {

  private static final int HITS_NEEDED_TO_WIN = 10;

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void playerDamageEvent(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) return;
    PracticePlayer damagedPlayer = get((Player) event.getEntity());
    if (!damagedPlayer.isInMatch())
      throw new IllegalStateException(
          "Attacked player should be in match but wasn't " + damagedPlayer.getName());

    if (!(event.getDamager() instanceof Player)) return;
    PracticePlayer damagerPlayer = get((Player) event.getDamager());

    if (!damagerPlayer.isInMatch())
      throw new IllegalStateException(
          "Attacking player should be in match but wasn't " + damagerPlayer.getName());
    Match match = damagerPlayer.getCurrentMatch();

    if (match.getLadderType() != LadderType.BOXING) {
      return;
    }

    PlayerStatisticsTracker statisticsTracker = match.getStatisticsTracker();
    if (statisticsTracker.getHits(damagerPlayer) >= HITS_NEEDED_TO_WIN) {
      event.setDamage(10000);
    } else {
      event.setDamage(0.01);
    }
  }
}
