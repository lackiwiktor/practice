package me.ponktacology.practice.match.pearl_cooldown;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.player.PracticePlayer;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class PearlCooldownTracker {

    private final Match match;
    private final Map<PracticePlayer, PearlCooldown> pearlCooldowns = Maps.newHashMap();

    public long getRemaining(PracticePlayer player) {
        return getCooldown(player).getRemaining();
    }

    public void reset(PracticePlayer player) {
        getCooldown(player).reset();
    }

    public void notifyPlayer(PracticePlayer player) {
        getCooldown(player).notifyPlayer();
    }

    public boolean hasExpired(PracticePlayer player) {
        return getCooldown(player).hasExpired();
    }

    private PearlCooldown getCooldown(PracticePlayer player) {
        Preconditions.checkArgument(match.isInMatch(player), "player is not in this match");

        return pearlCooldowns.computeIfAbsent(player, PearlCooldown::new);
    }

}
