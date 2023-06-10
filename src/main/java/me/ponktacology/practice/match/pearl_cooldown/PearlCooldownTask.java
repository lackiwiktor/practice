package me.ponktacology.practice.match.pearl_cooldown;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PearlCooldownTask implements Runnable {

  @Override
  public void run() {
    MatchService matchService = Practice.getService(MatchService.class);
    for (PracticePlayer player : Practice.getService(PlayerService.class).getAll()) {
      if (!matchService.isInMatch(player)) continue;
      Match match = matchService.getPlayerMatch(player);
      PearlCooldownTracker cooldownTracker = match.getCooldownTracker();
      if (cooldownTracker.hasExpired(player)) {
        cooldownTracker.notifyPlayer(player);
      } else {
        long remainingCooldown = cooldownTracker.getRemaining(player);
        int seconds = (int) Math.round(remainingCooldown / 1_000.0);
        Player bukkitPlayer = player.getPlayer();
        bukkitPlayer.setLevel(seconds);
        bukkitPlayer.setExp(remainingCooldown / 16_000.0F);
      }
    }
  }
}
