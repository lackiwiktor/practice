package me.ponktacology.practice.invitation.duel;

import lombok.Data;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.ladder.Ladder;
import org.jetbrains.annotations.Nullable;

@Data
public class DuelRequest<V> extends Request {

    private final V inviter;
    private final Ladder ladder;
    private final @Nullable MatchArena arena;

}
