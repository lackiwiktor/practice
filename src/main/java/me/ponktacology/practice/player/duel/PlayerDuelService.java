package me.ponktacology.practice.player.duel;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.PracticePreconditions;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.invitation.duel.DuelService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.player.PracticePlayer;
import org.jetbrains.annotations.Nullable;

public class PlayerDuelService extends DuelService<PracticePlayer, PlayerDuelRequest> {

  @Override
  protected void handleAccept(PracticePlayer inviter, PracticePlayer invitee, Ladder ladder, @Nullable MatchArena arena) {
    Practice.getService(MatchService.class)
        .start(ladder, arena,false, true, SoloTeam.of(inviter), SoloTeam.of(invitee));
  }

  @Override
  protected boolean canSendDuel(PracticePlayer inviter, PracticePlayer invitee) {
    return PracticePreconditions.canSendPlayerDuel(inviter, invitee);
  }

  @Override
  protected boolean canAcceptDuel(PracticePlayer inviter, PracticePlayer invitee) {
    return PracticePreconditions.canAcceptPlayerDuel(inviter, invitee);
  }

  @Override
  protected PlayerDuelRequest createDuelRequest(PracticePlayer inviter, Ladder ladder, @Nullable MatchArena arena) {
    return new PlayerDuelRequest(inviter, ladder, arena);
  }
}
