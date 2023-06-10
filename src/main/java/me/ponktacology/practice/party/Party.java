package me.ponktacology.practice.party;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.PracticePreconditions;
import me.ponktacology.practice.event.EventParticipant;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.invitation.Invitation;
import me.ponktacology.practice.invitation.InvitationService;
import me.ponktacology.practice.invitation.duel.DuelInvitable;
import me.ponktacology.practice.invitation.duel.Request;
import me.ponktacology.practice.party.duel.PartyDuelRequest;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;
import me.ponktacology.practice.util.message.Recipient;
import me.ponktacology.practice.util.visibility.VisibilityService;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Data
public class Party implements DuelInvitable<Party, PartyDuelRequest>, Recipient, EventParticipant {
  private final UUID id = UUID.randomUUID(); // party-id
  private final Set<PracticePlayer> members = Sets.newHashSet();
  private final Set<PartyDuelRequest> duelRequests = Sets.newConcurrentHashSet();
  private final Set<PartyInviteRequest> requests = Sets.newConcurrentHashSet();
  private PracticePlayer leader;
  private boolean disbanded;

  Party(PracticePlayer leader) {
    this.leader = leader;
    members.add(leader);
    Practice.getService(PartyService.class).updatePlayerParty(leader, this);
    Practice.getService(HotBarService.class).apply(leader);
  }

  private boolean addToParty(PracticePlayer player) {
    if (!PracticePreconditions.canJoinParty(player, this)) {
      return false;
    }

    members.add(player);
    Practice.getService(PartyService.class).updatePlayerParty(player, this);
    broadcast(Messages.PARTY_PLAYER_JOINED.match("{player}", player.getName()));

    if (player.isInLobby()) {
      update(player);
    }

    return true;
  }

  public void leaveFromParty(PracticePlayer player, RemoveReason reason) {
    Preconditions.checkArgument(members.contains(player), "player is not in this party");
    if (size() == 1) {
      disband();
      return;
    }

    removePlayer(player, reason);

    if (isLeader(player)) {
      leader = members.toArray(new PracticePlayer[0])[(int) (members.size() * Math.random())];
      Messenger.message(leader, "You are now the new leader of the party.");

      if (leader.isInLobby()) {
        Practice.getService(HotBarService.class).apply(leader);
      }
    }
  }

  public void inviteToParty(PracticePlayer inviter, PracticePlayer invitee) {
    Invitation invitation =
            new Invitation("You have been invited to " + getName() + " party.", invitee) {
              @Override
              protected boolean onAccept() {
                return addToParty(invitee);
              }

              @Override
              protected void onDecline() {
                requests.removeIf(it -> it.getInvitee().equals(invitee));
              }
            };

    requests.add(new PartyInviteRequest(invitee));
    Practice.getService(InvitationService.class).invite(invitee, invitation);
  }

  public void disband() {
    for (PracticePlayer player : members) {
      removePlayer(player, RemoveReason.DISBAND);
    }

    Practice.getService(PartyService.class).remove(this);

    if (!isInLobby()) return;
    for (PracticePlayer player : members) {
      Practice.getService(HotBarService.class).apply(player);
      for (PracticePlayer other : members) {
        Practice.getService(VisibilityService.class).update(player, other);
        Practice.getService(VisibilityService.class).update(other, player);
      }
    }
  }

  private void removePlayer(PracticePlayer player, RemoveReason reason) {
    broadcast(reason.message.match("{player}", player.getName()));
    members.remove(player);
    Practice.getService(PartyService.class).updatePlayerParty(player, null);
    if (player.isInLobby()) {
      update(player);
      Practice.getService(HotBarService.class).apply(player);
    }
  }

  public boolean isLeader(PracticePlayer session) {
    return leader.equals(session);
  }

  public boolean hasPlayer(PracticePlayer player) {
    return members.contains(player);
  }

  public boolean isPlayerInvited(PracticePlayer invitee) {
    return requests.stream().anyMatch(it -> it.getInvitee().equals(invitee));
  }

  public void invalidateInviteRequests() {
    requests.removeIf(Request::hasExpired);
  }

  public String getName() {
    return leader.getName();
  }

  public int size() {
    return members.size();
  }

  private void broadcast(String message) {
    members.forEach(it -> Messenger.message(it, message));
  }

  @Override
  public void receive(String message) {
    leader.receive(message);
  }

  @Override
  public void addDuelRequest(PartyDuelRequest request) {
    duelRequests.add(request);
  }

  @Override
  public boolean hasDuelRequest(DuelInvitable inviter) {
    return duelRequests.stream().anyMatch(it -> it.getInviter().equals(inviter));
  }

  @Override
  public void clearDuelRequests(DuelInvitable inviter) {
    duelRequests.removeIf(it -> it.getInviter().equals(inviter));
  }

  @Override
  public void invalidateDuelRequests() {
    duelRequests.removeIf(Request::hasExpired);
  }

  @Override
  public @Nullable PartyDuelRequest getDuelRequest(DuelInvitable inviter) {
    return duelRequests.stream()
        .filter(it -> it.getInviter().equals(inviter))
        .findFirst()
        .orElse(null);
  }

  public boolean isInLobby() {
    return members.stream().allMatch(PracticePlayer::isInLobby);
  }

  @Override
  public void receiveInvite(TextComponent component) {
    leader.receiveInvite(component);
  }

  @Override
  public void returnToLobby() {
    members.forEach(it -> it.returnToLobby());
  }

  private void update(PracticePlayer player) {
    for (PracticePlayer partyMember : members) {
      Practice.getService(VisibilityService.class).update(player, partyMember);
      Practice.getService(VisibilityService.class).update(partyMember, player);
    }

    Practice.getService(HotBarService.class).apply(player);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Party party = (Party) o;
    return Objects.equals(id, party.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @RequiredArgsConstructor
  @Getter
  public enum RemoveReason {
    LEFT(Messages.PARTY_PLAYER_LEFT),
    KICKED(Messages.PARTY_PLAYER_WAS_KICKED),
    DISBAND(Messages.PARTY_DISBANDED),
    DISCONNECTED(Messages.PARTY_PLAYER_DISCONNECTED);

    private final Messages message;
  }
}
