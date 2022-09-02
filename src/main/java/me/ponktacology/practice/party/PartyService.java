package me.ponktacology.practice.party;

import com.google.common.collect.Sets;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.invitation.Invitation;
import me.ponktacology.practice.invitation.InvitationService;
import me.ponktacology.practice.party.command.PartyCommands;
import me.ponktacology.practice.party.listener.PartyListener;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;
import me.ponktacology.practice.util.visibility.VisibilityService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PartyService extends Service {

  private final Set<Party> parties = Sets.newHashSet();

  @Override
  public void configure() {
    addListener(new PartyListener(this));
    addCommand(new PartyCommands(this));

    Runnable invalidatePartyRequestsTask =
        () -> {
          for (Party party : parties) {
            party.invalidateInviteRequests();
            party.invalidateDuelRequests();
          }
        };

    registerTask(invalidatePartyRequestsTask, 1L, TimeUnit.SECONDS, true);
  }

  public void add(Party party) {
    parties.add(party);
  }

  public void remove(Party party) {
    parties.remove(party);
  }

  public Set<Party> getAll() {
    return parties.stream().filter(it -> !it.isDisbanded()).collect(Collectors.toSet());
  }

  public void createParty(PracticePlayer leader) {
    if (leader.hasParty()) {
      Messenger.messageError(leader, "You already are in a party.");
      return;
    }

    Party party = new Party(leader);
    add(party);
    check(party, leader);
  }

  public void disbandParty(PracticePlayer senderPlayer, Party party) {
    if (!senderPlayer.isPartyLeader()) {
      Messenger.messageError(senderPlayer, "You are not the leader of the party.");
      return;
    }

    for (PracticePlayer player : party.getMembers()) {
      player.removeFromParty();
    }

    party.disband();

    if (party.isInLobby()) {
      for (PracticePlayer player : party.getMembers()) {
        Practice.getService(HotBarService.class).apply(player);
        for (PracticePlayer other : party.getMembers()) {
          Practice.getService(VisibilityService.class).update(player, other);
          Practice.getService(VisibilityService.class).update(other, player);
        }
      }
    }

    remove(party);
  }

  public void inviteToParty(PracticePlayer inviter, PracticePlayer invitee, Party party) {
    Invitation invitation =
        new Invitation("You have been invited to " + party.getName() + " party.", invitee) {
          @Override
          protected boolean onAccept() {
            return acceptInvite(party, invitee);
          }

          @Override
          protected void onDecline() {
            party.removePlayerInvite(invitee);
          }
        };

    party.invite(invitee);
    Practice.getService(InvitationService.class).invite(invitee, invitation);
  }

  public boolean acceptInvite(Party party, PracticePlayer player) {
    return addToParty(party, player);
  }

  private boolean check(Party party, PracticePlayer player) {
    if (!player.isInLobby()) {
      Messenger.messageError(player, "You must be in the lobby in order to accept a party invite");
      return false;
    }

    if (party.isDisbanded()) {
      Messenger.messageError(player, "This party has been disbanded.");
      return true;
    }

    if (player.hasParty()) {
      Messenger.messageError(player, "You already are in a party.");
      return false;
    }

    if (!party.isInLobby()) {
      Messenger.messageError(player, "Party must be in lobby in order to join it.");
      return false;
    }

    party.addPlayer(player);
    player.addToParty(party);
    update(player, party);
    return true;
  }

  private boolean addToParty(Party party, PracticePlayer player) {
    if (!party.isPlayerInvited(player)) {
      Messenger.messageError(
          player, "You have not been invited to this party or the request has expired.");
      return false;
    }

    return check(party, player);
  }

  public void kickFromParty(Party party, PracticePlayer session) {
    party.removePlayer(session, Party.RemoveReason.KICKED);
    session.removeFromParty();

    if (session.isInLobby()) {
      update(session, party);
      Practice.getService(HotBarService.class).apply(session);
    }
  }

  public void leaveFromParty(Party party, PracticePlayer session, Party.RemoveReason reason) {
    if (party.size() == 1) {
      disbandParty(session, party);
      return;
    }

    if (session.isPartyLeader()) {
      List<PracticePlayer> membersLeft = new ArrayList<>(party.getMembers());
      membersLeft.remove(session);
      int random = ThreadLocalRandom.current().nextInt(0, membersLeft.size());
      PracticePlayer newLeader = membersLeft.toArray(new PracticePlayer[0])[random];
      party.setLeader(newLeader);
      Messenger.message(newLeader, "You are now the new leader of the party.");

      if (newLeader.isInLobby()) {
        Practice.getService(HotBarService.class).apply(newLeader);
      }
    }

    party.removePlayer(session, reason);
    session.removeFromParty();

    if (session.isInLobby()) {
      update(session, party);
      Practice.getService(HotBarService.class).apply(session);
    }
  }

  private void update(PracticePlayer player, Party party) {
    for (PracticePlayer partyMember : party.getMembers()) {
      Practice.getService(VisibilityService.class).update(player, partyMember);
      Practice.getService(VisibilityService.class).update(partyMember, player);
    }

    Practice.getService(HotBarService.class).apply(player);
  }
}
