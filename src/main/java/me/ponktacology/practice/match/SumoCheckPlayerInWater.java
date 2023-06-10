package me.ponktacology.practice.match;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import org.bukkit.Material;

import java.util.Collections;

public class SumoCheckPlayerInWater implements Runnable {

  @Override
  public void run() {
    for (Match match : Practice.getService(MatchService.class).getAll()) {
      if (!match.getLadder().isSumo() || match.getState() != MatchState.IN_PROGRESS) continue;
      PlayerInfoTracker infoTracker = match.getInfoTracker();
      for (Team team : match.getTeams()) {
        for (PracticePlayer player : match.getOnlinePlayers(team)) {
          if (!infoTracker.isAlive(player)) continue;
          Material blockType = player.getLocation().getBlock().getType();

          if (blockType.toString().contains("WATER")) {
            match.markAsDead(player, Collections.emptyList());
          }
        }
      }
    }
  }
}
