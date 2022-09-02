package me.ponktacology.practice.match.pearl_cooldown;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PearlCooldownTask implements Runnable {

    private final PlayerService partyDuelService;

    @Override
    public void run() {
        for (PracticePlayer player : partyDuelService.getAll()) {
            if (!player.isOnline() || !player.isInMatch()) continue;
            Match match = player.getCurrentMatch();
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
