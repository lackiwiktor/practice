package me.ponktacology.practice.queue.command;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.queue.QueueService;
import me.ponktacology.practice.queue.menu.QueueMenu;
import me.ponktacology.practice.util.message.Messenger;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

public class QueueCommands extends PlayerCommands {

  @Command("ranked")
  public void ranked(@Sender Player sender) {
    joinQueue(sender, true);
  }

  @Command("autoqueue")
  public void autoqueue(@Sender Player sender) {
    PracticePlayer senderSession = get(sender);

    if (!senderSession.isInLobby()) {
      return;
    }

    Practice.getService(QueueService.class).getQueues(false).stream()
        .findFirst()
        .ifPresent(it -> it.addPlayer(senderSession));
  }

  @Command("unranked")
  public void unranked(@Sender Player sender) {
    joinQueue(sender, false);
  }

  @Command("leavequeue")
  public void leaveQueue(@Sender Player sender) {
    PracticePlayer senderSession = get(sender);

    if (!senderSession.isInQueue()) {
      Messenger.messageError(sender, "You are not in a queue.");
      return;
    }

    senderSession.removeFromQueue(true);
  }

  private void joinQueue(Player sender, boolean ranked) {
    PracticePlayer senderSession = get(sender);

    if (!canJoinQueue(senderSession)) {
      return;
    }

    new QueueMenu(ranked, senderSession).openMenu(sender);
  }

  private boolean canJoinQueue(PracticePlayer player) {
    if (!player.isInLobby()) {
      Messenger.messageError(player, "You can join a queue only in the lobby.");
      return false;
    }

    if (player.hasParty()) {
      Messenger.messageError(player, "You can join a queue while being in a party.");
      return false;
    }

    return true;
  }
}
