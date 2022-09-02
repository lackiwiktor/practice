package me.ponktacology.practice.match.listener;

import me.ponktacology.practice.ladder.LadderType;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.event.MatchStartCountdownEvent;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.LocationUtil;
import me.ponktacology.practice.util.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;

public class SumoMatchListener extends PracticePlayerListener {

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    if (!LocationUtil.hasChanged(event.getFrom(), event.getTo(), true)) return;

    PracticePlayer damagedPlayer = get(event.getPlayer());
    if (!damagedPlayer.isInMatch()) return;
    Match match = damagedPlayer.getCurrentMatch();

    if (match.getLadderType() != LadderType.SUMO) {
      return;
    }

    if (match.getState() != MatchState.IN_PROGRESS) {
      return;
    }

    Material blockType = event.getTo().getBlock().getType();

    if (blockType == Material.STATIONARY_WATER || blockType == Material.WATER) {
      PlayerInfoTracker infoTracker = match.getInfoTracker();

      if (infoTracker.isAlive(damagedPlayer)) {
        match.onPlayerDeath(damagedPlayer, Collections.emptyList());
      }
    }
  }

  @EventHandler
  public void onMatchStartCountdown(MatchStartCountdownEvent event) {
    Match match = event.getMatch();

    if (match.getLadderType() != LadderType.SUMO) {
      return;
    }

    for (Team team : match.getTeams()) {
      for (PracticePlayer player : team.getOnlinePlayers()) {
        PlayerUtil.denyMovement(player.getPlayer());
      }
    }
  }

  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();

    if (match.getLadderType() != LadderType.SUMO) {
      return;
    }

    for (Team team : match.getTeams()) {
      for (PracticePlayer player : team.getOnlinePlayers()) {
        PlayerUtil.allowMovement(player.getPlayer());
      }
    }
  }
}
