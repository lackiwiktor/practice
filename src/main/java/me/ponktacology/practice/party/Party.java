package me.ponktacology.practice.party;

import com.google.common.collect.Sets;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.event.Event;
import me.ponktacology.practice.event.EventParticipant;
import me.ponktacology.practice.invitation.duel.DuelInvitable;
import me.ponktacology.practice.invitation.duel.Request;
import me.ponktacology.practice.party.duel.PartyDuelRequest;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;
import me.ponktacology.practice.util.message.Recipient;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
public class Party implements DuelInvitable<Party, PartyDuelRequest>, Recipient, EventParticipant {
  private final UUID id = UUID.randomUUID(); // party-id
  private final Set<PracticePlayer> members = Sets.newHashSet();
  private final Set<PartyDuelRequest> duelRequests = Sets.newConcurrentHashSet();
  private final Set<PartyInviteRequest> requests = Sets.newConcurrentHashSet();
  private PracticePlayer leader;
  private boolean disbanded;

  public Party(PracticePlayer leader) {
    this.leader = leader;
  }

  public boolean isLeader(PracticePlayer session) {
    return leader.equals(session);
  }

  public void addPlayer(PracticePlayer player) {
    broadcast(Messages.PARTY_PLAYER_JOINED.match("{player}", player.getName()));
    members.add(player);
  }

  public void removePlayer(PracticePlayer player, RemoveReason reason) {
    members.remove(player);
    player.removeFromParty();
    broadcast(reason.message.match("{player}", player.getName()));
  }

  public boolean hasPlayer(PracticePlayer player) {
    return members.contains(player);
  }

  public void invite(PracticePlayer invitee) {
    requests.add(new PartyInviteRequest(invitee));
  }

  public boolean isPlayerInvited(PracticePlayer invitee) {
    return requests.stream().anyMatch(it -> it.getInvitee().equals(invitee));
  }

  public void disband() {
    disbanded = true;
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

  public void removePlayerInvite(PracticePlayer invitee) {
    requests.removeIf(it -> it.getInvitee().equals(invitee));
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
    return members.stream().noneMatch(PracticePlayer::isInMatch);
  }

  @Override
  public void receiveInvite(Component component) {
    leader.receiveInvite(component);
  }

  @Override
  public void setCurrentEvent(Event<?> event) {
    members.forEach(it -> it.setCurrentEvent(event));
  }

  @Override
  public void returnToLobby() {
    members.forEach(it -> it.returnToLobby());
  }

  @RequiredArgsConstructor
  @Getter
  public enum RemoveReason {
    LEFT(Messages.PARTY_PLAYER_LEFT),
    KICKED(Messages.PARTY_PLAYER_WAS_KICKED),
    DISCONNECTED(Messages.PARTY_PLAYER_DISCONNECTED);

    private final Messages message;
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
}
