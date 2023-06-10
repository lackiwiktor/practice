package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.player.PracticePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ComboMatchListener implements Listener {

  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();

    int noDamageTicks = match.getLadder().isCombo() ? 3 : 20;

    match.forEachTeam(
        team -> {
          for (PracticePlayer practicePlayer : match.getOnlinePlayers(team)) {
            Player bukkitPlayer = practicePlayer.getPlayer();
            bukkitPlayer.setNoDamageTicks(noDamageTicks);
          }
        });
  }
}
