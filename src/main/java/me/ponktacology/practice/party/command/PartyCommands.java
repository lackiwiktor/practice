package me.ponktacology.practice.party.command;

import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
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
import lombok.RequiredArgsConstructor;
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

    if (!practicePlayer.hasParty()) {
      Messenger.messageError(practicePlayer, "You do not have a party.");
      return;
    }

    Party party = practicePlayer.getParty();
    partyService.leaveFromParty(party, practicePlayer, Party.RemoveReason.LEFT);
  }

  @Command("party disband")
  public void disband(@Sender Player sender) {
    PracticePlayer leader = get(sender);

    if (!leader.hasParty()) {
      Messenger.messageError(leader, "You do not have a party.");
      return;
    }

    if (!leader.isPartyLeader()) {
      Messenger.messageError(leader, "You must be the leader of the party in order to disband it.");
      return;
    }

    Party party = leader.getParty();
    partyService.disbandParty(leader, party);
  }

  @Command("party invite")
  public void invite(@Sender Player sender, @Name("player") PracticePlayer invitee) {
    PracticePlayer inviter = get(sender);

    if (!inviter.hasParty()) {
      Messenger.messageError(inviter, "You do not have a party.");
      return;
    }

    if (inviter.equals(invitee)) {
      Messenger.messageError(inviter, "You can't invite yourself to a party.");
      return;
    }

    if (invitee.hasParty()) {
      Messenger.messageError(inviter, "This player already has a party.");
      return;
    }

    Party party = inviter.getParty();

    if (party.isPlayerInvited(invitee)) {
      Messenger.messageError(inviter, "This player has already been invited to the party.");
      return;
    }

    partyService.inviteToParty(inviter, invitee, party);
  }

  @Command("party kick")
  public void kick(@Sender Player sender, @Name("player") PracticePlayer member) {
    PracticePlayer leader = get(sender);

    if (leader.equals(member)) {
      Messenger.messageError(
          leader,
          "You can't kick yourself from a party, if you wish to disband it use /party disband.");
      return;
    }

    if (!leader.hasParty()) {
      Messenger.messageError(leader, "You do not have a party.");
      return;
    }

    Party party = leader.getParty();
    partyService.kickFromParty(party, member);
  }

  @Command("party info")
  public void info(@Sender Player sender, @Optional @Name("player") PracticePlayer player) {
    if (player != null) {
      if (!player.hasParty()) {
        Messenger.messageError(sender, "This player is not in a party.");
        return;
      }
    } else {
      PracticePlayer senderPlayer = get(sender);

      if (!senderPlayer.hasParty()) {
        Messenger.messageError(sender, "You are not in a party.");
        return;
      }

      player = senderPlayer;
    }

    Party party = player.getParty();
    Messenger.message(sender, "Party Members: " + party.getMembers().size());
  }

  @Command("party event")
  public void info(@Sender Player sender) {
    PracticePlayer leader = get(sender);

    if (!leader.hasParty()) {
      Messenger.messageError(leader, "You do not have a party.");
      return;
    }

    Party party = leader.getParty();

    if (!party.isLeader(leader)) {
      Messenger.messageError(leader, "You must be a leader of the party to start party event.");
      return;
    }

    if (!party.isInLobby()) {
      Messenger.messageError(leader, "You must be in lobby in order to start a party event");
      return;
    }

    new PartyEventMenu(party).openMenu(sender);
  }

  @Command("party members")
  public void members(@Sender Player sender) {
    PracticePlayer inviter = get(sender);

    if (!inviter.hasParty()) {
      Messenger.messageError(inviter, "You do not have a party.");
      return;
    }

    if (!inviter.isInLobby()) {
      Messenger.messageError(inviter, "You must be in the lobby in order to view party members.");
      return;
    }

    new PartyMembersMenu(inviter.getParty()).openMenu(sender);
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
    PracticePlayer leader = get(sender);
    if (!leader.hasParty()) {
      Messenger.messageError(leader, "You do not have a party.");
      return;
    }

    if (!leader.isPartyLeader()) {
      Messenger.messageError(leader, "You must be a leader of the party to start party event.");
      return;
    }

    Party inviter = leader.getParty();

    PartyDuelService partyDuelService = Practice.getService(PartyDuelService.class);
    if (ladder != null) {
      partyDuelService.invite(
          inviter,
          invitee,
          ladder,
          Messages.PARTY_DUEL_INVITATION.match(
              new MessagePattern("{party}", inviter.getName()),
              new MessagePattern("{ladder}", ladder.getDisplayName())));
    } else {
      new KitChooseMenu(
              l ->
                  partyDuelService.invite(
                      inviter,
                      invitee,
                      l,
                      Messages.PARTY_DUEL_INVITATION.match(
                          new MessagePattern("{party}", inviter.getName()),
                          new MessagePattern("{ladder}", l.getDisplayName()))))
          .openMenu(sender);
    }
  }

  private String getShortPartyInfo(Party party) {
    return party.getName() + " (" + party.getMembers().size() + "/20";
  }
}
