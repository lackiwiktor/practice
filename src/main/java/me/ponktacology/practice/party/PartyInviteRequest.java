package me.ponktacology.practice.party;

import me.ponktacology.practice.invitation.duel.Request;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.TimeUtil;
import lombok.Data;

@Data
public class PartyInviteRequest extends Request {

    private final PracticePlayer invitee;

    @Override
    public boolean hasExpired() {
        return TimeUtil.elapsed(createdAt) > 120_000L;
    }
}
