package me.ponktacology.practice.match.command;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Cache;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.arena.ArenaChooseMenu;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.kit.KitChooseMenu;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.RematchData;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.team.type.SoloTeam;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.duel.PlayerDuelRequest;
import me.ponktacology.practice.player.duel.PlayerDuelService;
import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.Messenger;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Optional;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.argument.Text;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MatchCommands extends PlayerCommands {

  private final MatchService matchService;

  @Command("printcache")
  @Permission("practice.admin")
  public void printCache(@Sender Player sender) {
    for (Service service : Practice.getServices()) {
      if (!(service instanceof Cache)) continue;
      Cache cache = (Cache) service;
      sender.sendMessage(service.getClass().getSimpleName() + " " + cache.getSize());
    }
  }

  @Command("asynctags test")
  @Permission("practice.admin")
  public void asyncTagsTest(
      @Sender Player sender,
      @Name("other") PracticePlayer player,
      @Name("prefix") String prefix,
      @Name("suffix") String suffix,
      @Name("priority") int priority) {
    sendNameTagPlayers(player.getPlayer(), prefix, suffix, sender, priority);
  }

  public static void sendNameTagPlayers(
      Player player, String prefix, String suffix, Player players, int priority) {}

  @Command("match list")
  @Permission("match.admin")
  public void matchList(@Sender Player sender) {
    sender.sendMessage(matchService.getAll().size() + " Matches");
    sender.sendMessage(
        matchService.getAll().stream()
            .map(
                it ->
                    it.getId()
                        + " "
                        + it.getState()
                        + " "
                        + it.getTeams().stream()
                            .map(Team::getName)
                            .collect(Collectors.joining(", ")))
            .collect(Collectors.joining("\n")));
  }

  @Command("match cancel")
  @Permission("match.admin")
  public void cancel(
      @Sender Player sender,
      @Name("player") PracticePlayer player,
      @Text @Optional("Cancelled by the staff member") @Name("reason") String reason) {
    if (!matchService.isInMatch(player)) {
      Messenger.messageError(sender, "Player is not in a match.");
      return;
    }

    Match match = matchService.getPlayerMatch(player);
    match.cancel(reason);
    Messenger.messageSuccess(sender, "Successfully cancelled this match.");
  }

  @Command("match ffa")
  @Permission("match.admin")
  public void ffa(
      @Sender Player sender,
      Ladder ladder,
      PracticePlayer p1,
      PracticePlayer p2,
      PracticePlayer p3) {
    matchService.start(ladder, null, SoloTeam.of(p1), SoloTeam.of(p2), SoloTeam.of(p3));
  }

  @Command(value = {"spectate", "spec"})
  public void specate(@Sender Player sender, @Name("player") PracticePlayer player) {
    PracticePlayer practicePlayer = get(sender);

    if (!matchService.isInMatch(player)) {
      Messenger.messageError(practicePlayer, "This player is not in a match right now.");
      return;
    }

    if (!practicePlayer.isInLobby()) {
      Messenger.messageError(sender, "You must be in a lobby in order to spectate.");
    }

    if (practicePlayer.isSpectating()) {
      Match currentMatch = practicePlayer.getCurrentlySpectatingMatch();
      currentMatch.stopSpectating(practicePlayer, practicePlayer.isSilentMode(), false);
    }

    Match match = matchService.getPlayerMatch(player);
    match.startSpectating(practicePlayer, player, false);
  }

  @Command("duel")
  public void duel(
      @Sender Player sender,
      @Name("player") PracticePlayer invitee,
      @Optional @Name("ladder") Ladder ladder) {
    PracticePlayer inviter = get(sender);

    if (inviter.equals(invitee)) {
      Messenger.messageError(inviter, "You can't invite yourself for a duel.");
      return;
    }

    if (ladder != null) {
      Practice.getService(PlayerDuelService.class)
          .invite(
              inviter,
              invitee,
              ladder,
              null,
              Messages.PLAYER_DUEL_INVITATION.match(
                  new MessagePattern("{player}", inviter.getName()),
                  new MessagePattern("{ping}", inviter.getPing()),
                  new MessagePattern("{ladder}", ladder.getDisplayName())));
    } else {
      new KitChooseMenu(
              false,
              chosenLadder ->
                  new ArenaChooseMenu(
                          true,
                          chosenArena ->
                              Practice.getService(PlayerDuelService.class)
                                  .invite(
                                      inviter,
                                      invitee,
                                      chosenLadder,
                                      chosenArena,
                                      Messages.PLAYER_DUEL_INVITATION.match(
                                          new MessagePattern("{player}", inviter.getName()),
                                          new MessagePattern("{ping}", inviter.getPing()),
                                          new MessagePattern(
                                              "{ladder}", chosenLadder.getDisplayName()))))
                      .openMenu(sender))
          .openMenu(sender);
    }
  }

  @Command("accept")
  public void accept(@Sender Player sender, @Name("player") PracticePlayer player) {
    PracticePlayer invitee = get(sender);

    if (!invitee.hasDuelRequest(player)) {
      Messenger.messageError(sender, "You have not received duel request from this player.");
      return;
    }

    PlayerDuelRequest duelRequest = invitee.getDuelRequest(player);
    Practice.getService(PlayerDuelService.class).acceptInvite(invitee, duelRequest);
  }

  @Command("stopspectating")
  public void stopSpectating(@Sender Player sender) {
    PracticePlayer senderSession = get(sender);

    if (!senderSession.isSpectating()) {
      Messenger.messageError(sender, "You are not spectating a match.");
      return;
    }

    senderSession.stopSpectating(senderSession.isSilentMode());
  }

  @Command("rematch")
  public void rematch(@Sender Player sender) {
    PracticePlayer practicePlayer = get(sender);

    if (!practicePlayer.isInLobby()) {
      Messenger.messageError(practicePlayer, "You must be in lobby in order to rematch someone.");
      return;
    }

    RematchData rematchData = practicePlayer.getRematchData();

    if (rematchData == null) {
      Messenger.messageError(practicePlayer, "You don't have a player to rematch.");
      return;
    }

    practicePlayer.runCommand(
        "duel " + rematchData.getPlayer().getName() + " " + rematchData.getLadder().getName());
  }
}
