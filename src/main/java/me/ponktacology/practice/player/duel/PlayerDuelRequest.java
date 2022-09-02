package me.ponktacology.practice.player.duel;

import me.ponktacology.practice.invitation.duel.DuelRequest;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.player.PracticePlayer;

public class PlayerDuelRequest extends DuelRequest<PracticePlayer> {

   // private @Nullable Arena arena;

    public PlayerDuelRequest(PracticePlayer inviter, Ladder ladder) {
        super(inviter, ladder);
    }
}
