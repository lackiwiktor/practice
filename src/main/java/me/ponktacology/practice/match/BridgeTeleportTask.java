package me.ponktacology.practice.match;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;

public class BridgeTeleportTask implements Runnable {

  @Override
  public void run() {
    for (Match match : Practice.getService(MatchService.class).getAll()) {
      if (!match.getLadder().isBridge()) continue;
      for (Team team : match.getTeams()) {
        for (PracticePlayer player : match.getOnlinePlayers(team)) {
          if (!match.getArena().isIn(player.getLocation())) {
            player.getPlayer().setFallDistance(0);
            player.teleport(match.getSpawnLocation(team));
          }
        }
      }
    }
  }
}
