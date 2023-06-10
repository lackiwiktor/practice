package me.ponktacology.practice.follow.command;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.follow.FollowService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;
import me.vaperion.blade.annotation.argument.Flag;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class FollowCommand extends PlayerCommands {

  private final FollowService followService;

  @Command("follow")
  public void follow(
      @Sender Player sender, @Name("player") PracticePlayer player, @Flag('s') boolean silent) {
    if (silent && !sender.hasPermission("follow.silent")) {
      Messenger.messageError(sender, "You can't silently follow a player.");
      return;
    }
    if (sender.equals(player)) {
      Messenger.messageError(sender, "You can't follow yourself.");
      return;
    }
    PracticePlayer senderPlayer = get(sender);
    if (!senderPlayer.isInLobby() && !senderPlayer.isSpectating()) {
      Messenger.messageError(sender, "You must be in a lobby to follow someone.");
      return;
    }
    if (followService.isFollowing(senderPlayer)) {
      Messenger.messageError(sender, "You are already following a player.");
      return;
    }
    followService.startFollowing(senderPlayer, player, silent);
  }

  @Command({"unfollow", "stopfollowing"})
  public void unfollow(@Sender Player sender) {
    PracticePlayer senderPlayer = get(sender);
    if (!followService.isFollowing(senderPlayer)) {
      Messenger.messageError(sender, "You are not following anyone.");
      return;
    }
    followService.stopFollowing(senderPlayer);
  }
}
