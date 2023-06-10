package me.ponktacology.practice.queue.command;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.PracticePreconditions;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.queue.Queue;
import me.ponktacology.practice.queue.QueueService;
import me.ponktacology.practice.queue.menu.QueueMenu;
import me.ponktacology.practice.queue.menu.SettingsMenu;
import me.ponktacology.practice.util.message.Messenger;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class QueueCommands extends PlayerCommands {

  private final QueueService queueService;

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
    PracticePlayer practicePlayer = get(sender);

    if (!queueService.isInQueue(practicePlayer)) {
      Messenger.messageError(sender, "You are not in a queue.");
      return;
    }

    Queue queue = queueService.getPlayerQueue(practicePlayer);
    queue.removePlayer(practicePlayer, true);
  }

  @Command("queuesettings")
  public void queueSettings(@Sender Player sender) {
    PracticePlayer practicePlayer = get(sender);

    new SettingsMenu(practicePlayer.getQueueSettings(), practicePlayer).openMenu(sender);
  }

  private void joinQueue(Player sender, boolean ranked) {
    PracticePlayer senderSession = get(sender);

    if (!PracticePreconditions.canJoinQueue(senderSession)) {
      return;
    }

    new QueueMenu(ranked, senderSession).openMenu(sender);
  }
}
