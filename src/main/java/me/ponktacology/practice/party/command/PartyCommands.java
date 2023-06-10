package me.ponktacology.practice.party.command;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.ArenaChooseMenu;
import me.ponktacology.practice.commands.PlayerCommands;
import me.ponktacology.practice.kit.KitChooseMenu;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.party.duel.PartyDuelService;
import me.ponktacology.practice.party.menu.PartyEventMenu;
import me.ponktacology.practice.party.menu.PartyMembersMenu;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.Messenger;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Optional;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class PartyCommands extends PlayerCommands {

  private final PartyService partyService;

  @Command("party create")
  public void create(@Sender Player sender) {
    PracticePlayer leader = get(sender);
    partyService.createParty(leader);
  }

  @Command("party leave")
  public void leave(@Sender Player sender) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);
    party.leaveFromParty(practicePlayer, Party.RemoveReason.LEFT);
  }

  @Command("party disband")
  public void disband(@Sender Player sender) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);

    if (!party.isLeader(practicePlayer)) {
      Messenger.messageError(
          practicePlayer, "You must be the leader of the party in order to disband it.");
      return;
    }

    party.disband();
  }

  @Command("party invite")
  public void invite(@Sender Player sender, @Name("player") PracticePlayer invitee) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);

    if (!party.isLeader(practicePlayer)) {
      Messenger.messageError(
          practicePlayer, "You must be the leader of the party in order to disband it.");
      return;
    }

    party.inviteToParty(practicePlayer, invitee);
  }

  @Command("party kick")
  public void kick(@Sender Player sender, @Name("player") PracticePlayer member) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);

    if (!party.isLeader(practicePlayer)) {
      Messenger.messageError(
          practicePlayer, "You must be the leader of the party in order to disband it.");
      return;
    }

    party.leaveFromParty(member, Party.RemoveReason.KICKED);
  }

  @Command("party info")
  public void info(@Sender Player sender, @Optional("self") @Name("player") PracticePlayer player) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(player)) {
      Messenger.messageError(sender, "This player is not in a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);
    Messenger.message(sender, "Party Members: " + party.getMembers().size());
  }

  @Command("party event")
  public void info(@Sender Player sender) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);

    if (!party.isLeader(practicePlayer)) {
      Messenger.messageError(
          practicePlayer, "You must be the leader of the party in order to disband it.");
      return;
    }

    if (!party.isInLobby()) {
      Messenger.messageError(
          practicePlayer, "You must be in lobby in order to start a party event");
      return;
    }

    new PartyEventMenu(party).openMenu(sender);
  }

  @Command("party members")
  public void members(@Sender Player sender) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    if (!practicePlayer.isInLobby()) {
      Messenger.messageError(
          practicePlayer, "You must be in the lobby in order to view party members.");
      return;
    }

    new PartyMembersMenu(partyService.getPlayerParty(practicePlayer)).openMenu(sender);
  }

  @Command("party list")
  public void list(@Sender Player sender) {
    for (Party party : partyService.getAll()) {
      sender.sendMessage(getShortPartyInfo(party));
    }
  }

  @Command("party duel")
  public void duel(
      @Sender Player sender,
      @Name("party") Party invitee,
      @Optional @Name("ladder") Ladder ladder) {
    PracticePlayer practicePlayer = get(sender);

    if (!partyService.hasParty(practicePlayer)) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = partyService.getPlayerParty(practicePlayer);

    if (!party.isLeader(practicePlayer)) {
      Messenger.messageError(
          practicePlayer, "You must be a leader of the party to start party event.");
      return;
    }

    PartyDuelService partyDuelService = Practice.getService(PartyDuelService.class);
    if (ladder != null) {
      partyDuelService.invite(
          party,
          invitee,
          ladder,
          null,
          Messages.PARTY_DUEL_INVITATION.match(
              new MessagePattern("{party}", party.getName()),
              new MessagePattern("{ladder}", ladder.getDisplayName())));
    } else {
      new KitChooseMenu(false,
              chosenLadder ->
                  new ArenaChooseMenu(true,
                          chosenArena ->
                              partyDuelService.invite(
                                  party,
                                  invitee,
                                  chosenLadder,
                                  chosenArena,
                                  Messages.PARTY_DUEL_INVITATION.match(
                                      new MessagePattern("{party}", party.getName()),
                                      new MessagePattern("{ladder}", chosenLadder.getDisplayName()))))
                      .openMenu(sender))
          .openMenu(sender);
    }
  }

  private String getShortPartyInfo(Party party) {
    return party.getName() + " (" + party.getMembers().size() + "/20";
  }
}
