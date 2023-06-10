package me.ponktacology.practice.follow.listener;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.follow.FollowService;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.event.MatchStartCountdownEvent;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

@RequiredArgsConstructor
public class FollowListener extends PracticePlayerListener {

  private final FollowService followService;

  @EventHandler
  public void onMatchStartCountdown(MatchStartCountdownEvent event) {
    Match match = event.getMatch();

    match.forEachTeam(
        team -> {
          for (PracticePlayer player : match.getOnlinePlayers(team)) {
            List<PracticePlayer> followers =
                Practice.getService(FollowService.class).getFollowers(player);
            for (PracticePlayer follower : followers) {
              match.startSpectating(follower, player, false);
            }
          }
        });
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    PracticePlayer player = get(event);
    followService.flushFollowers(player);
  }
}
