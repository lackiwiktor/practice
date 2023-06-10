package me.ponktacology.practice.event;

import com.google.common.collect.Sets;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.util.visibility.VisibilityService;
import lombok.Data;
import org.bukkit.Location;

import java.util.Set;
import java.util.UUID;

@Data
public abstract class Event<V extends EventParticipant> {

  private final UUID id = UUID.randomUUID();
  private final EventType type;
  protected final Set<V> participants = Sets.newHashSet();
  protected final Set<PracticePlayer> spectators = Sets.newHashSet();
  private boolean finished = false;
  private final boolean allowSpectators;

  public Event(EventType type, boolean allowSpectators) {
    this.type = type;
    this.allowSpectators = allowSpectators;
    Practice.getService(EventService.class).addEvent(this);
  }

  public boolean add(V participant) {
    if (!canJoin(participant)) {
      return false;
    }
    participants.add(participant);
    return true;
  }

  public void remove(V participant) {
    participants.remove(participant);
    participant.returnToLobby();
  }

  protected abstract boolean canJoin(V participant);

  protected abstract void init();

  protected abstract void cancel();

  public boolean isFinished() {
    return finished;
  }

  public void addSpectator(PracticePlayer player) {
    if (!allowSpectators) throw new UnsupportedOperationException("spectators are not allowed");
    spectators.add(player);
    prepareSpectator(player);
  }

  protected void prepareSpectator(PracticePlayer player) {
    spectators.add(player);
    player.teleport(getSpectatingLocation());

    player.setState(PlayerState.SPECTATING);
    Practice.getService(HotBarService.class).apply(player);
    Practice.getService(VisibilityService.class).update(player);
  }

  protected Location getSpectatingLocation() {
    throw new UnsupportedOperationException("unsupported");
  }

  public void removeSpectator(PracticePlayer player) {
    Practice.getService(LobbyService.class).moveToLobby(player);
  }

  protected void finish() {
    finished = true;
    Practice.getService(EventService.class).removeEvent(this);
  }
}
