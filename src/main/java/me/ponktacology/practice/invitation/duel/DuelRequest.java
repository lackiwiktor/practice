package me.ponktacology.practice.invitation.duel;

import me.ponktacology.practice.ladder.Ladder;
import lombok.Data;

@Data
public class DuelRequest<V> extends Request {

    private final V inviter;
    private final Ladder ladder;

}
