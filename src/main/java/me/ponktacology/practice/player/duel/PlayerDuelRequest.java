package me.ponktacology.practice.player.duel;

import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.invitation.duel.DuelRequest;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.player.PracticePlayer;
import org.jetbrains.annotations.Nullable;

public class PlayerDuelRequest extends DuelRequest<PracticePlayer> {

   // private @Nullable Arena arena;

    public PlayerDuelRequest(PracticePlayer inviter, Ladder ladder, @Nullable MatchArena arena) {
        super(inviter, ladder, arena);
    }
}
