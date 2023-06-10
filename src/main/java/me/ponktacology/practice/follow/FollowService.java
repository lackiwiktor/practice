package me.ponktacology.practice.follow;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.follow.command.FollowCommand;
import me.ponktacology.practice.follow.listener.FollowListener;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;
import me.ponktacology.practice.util.visibility.VisibilityService;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FollowService extends Service {
  private final Map<PracticePlayer, PracticePlayer> followerToPlayerMap = Maps.newConcurrentMap();

  @Override
  protected void configure() {
    addListener(new FollowListener(this));
    addCommand(new FollowCommand(this));
  }

  @Override
  public void stop() {
    followerToPlayerMap.clear();
  }

  public void startFollowing(PracticePlayer follower, PracticePlayer player, boolean silent) {
    followerToPlayerMap.put(follower, player);

    MatchService matchService = Practice.getService(MatchService.class);
    boolean isInMatch = matchService.isInMatch(player);
    if (follower.isSpectating()) {
      Match match = follower.getCurrentlySpectatingMatch();

      if (!match.isParticipating(player)) {
        match.stopSpectating(follower, player.isSilentMode(), !isInMatch);
      }
    }

    if (isInMatch) {
      Match match = matchService.getPlayerMatch(player);
      match.startSpectating(follower, player, silent);
    } else {
      Practice.getService(HotBarService.class).apply(follower);
      Practice.getService(VisibilityService.class).update(follower, player);
    }

    Messenger.message(follower, Messages.STARTED_FOLLOWING.match("{player}", player.getName()));
  }

  public void stopFollowing(PracticePlayer followerPlayer) {
    PracticePlayer player = followerToPlayerMap.remove(followerPlayer);

    if (followerPlayer.isSpectating()) {
      Match match = followerPlayer.getCurrentlySpectatingMatch();
      if (match.isParticipating(player)) {
        match.stopSpectating(followerPlayer, player.isSilentMode(), true);
      }
    } else {
      Practice.getService(HotBarService.class).apply(followerPlayer);
      Practice.getService(VisibilityService.class).update(followerPlayer, player);
    }

    Messenger.message(
        followerPlayer, Messages.STOPPED_FOLLOWING.match("{player}", player.getName()));
  }

  public boolean isFollowing(PracticePlayer player) {
    return followerToPlayerMap.containsKey(player);
  }

  public @Nullable PracticePlayer getFollowingPlayer(PracticePlayer player) {
    return followerToPlayerMap.get(player);
  }

  public List<PracticePlayer> getFollowers(PracticePlayer player) {
    return followerToPlayerMap.entrySet().stream()
        .filter(it -> it.getValue().equals(player))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  public void flushFollowers(PracticePlayer player) {
    followerToPlayerMap.remove(player);

    for (PracticePlayer follower : getFollowers(player)) {
      stopFollowing(follower);
    }
  }
}
