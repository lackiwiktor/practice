package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.event.MatchPlayerDamageByPlayerEvent;
import me.ponktacology.practice.match.event.MatchStartCountdownEvent;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.PlayerUtil;
import org.bukkit.event.EventHandler;

public class SumoMatchListener extends PracticePlayerListener {

  @EventHandler(ignoreCancelled = true)
  public void playerDamageEvent(MatchPlayerDamageByPlayerEvent event) {
    if (event.getMatch().getLadder().isSumo()) {
      event.getBukkitEvent().setDamage(0);
    }
  }

  @EventHandler
  public void onMatchStartCountdown(MatchStartCountdownEvent event) {
    Match match = event.getMatch();

    if (!match.getLadder().isSumo()) {
      return;
    }

    for (Team team : match.getTeams()) {
      for (PracticePlayer player : match.getOnlinePlayers(team)) {
        PlayerUtil.denyMovement(player.getPlayer());
      }
    }
  }

  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();

    if (!match.getLadder().isSumo()) {
      return;
    }

    for (Team team : match.getTeams()) {
      for (PracticePlayer player : match.getOnlinePlayers(team)) {
        PlayerUtil.allowMovement(player.getPlayer());
      }
    }
  }
}
