package me.ponktacology.practice.event.type;

import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.thimble.ThimbleArena;
import me.ponktacology.practice.event.Event;
import me.ponktacology.practice.event.EventType;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.TaskDispatcher;
import me.ponktacology.practice.util.message.Messenger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Thimble extends Event<PracticePlayer> {

  private final Queue<PracticePlayer> jumpQueue = new LinkedList<>();
  private final ThimbleLogicTask logicTask = new ThimbleLogicTask(this);
  private final ThimbleArena arena;
  private final int requiredParticipants;

  private @Setter ThimbleState state;
  private int round;
  private int initialSize;
  private @Nullable PracticePlayer currentJumper;

  public Thimble(ThimbleArena arena, int requiredParticipants) {
    super(EventType.THIMBLE, true);
    this.arena = arena;
    this.requiredParticipants = requiredParticipants;
  }

  // TODO: Change messages
  protected boolean canJoin(PracticePlayer party) {
    if (state != ThimbleState.WAITING_FOR_PLAYERS && state != ThimbleState.START_COUNTODWN) {
      Messenger.messageError(party, Messages.TOURNAMENT_ALREADY_STARTED.get());
      return false;
    }

    if (participants.size() >= 36) {
      Messenger.messageError(party, Messages.TOURNAMENT_FULL.get());
      return false;
    }

    return true;
  }

  @Override
  public void init() {
    setState(ThimbleState.WAITING_FOR_PLAYERS, 1);
    TaskDispatcher.scheduleSync(logicTask, 1L, TimeUnit.SECONDS);
  }

  void startNextRound() {
    if (round == 0) {
      initialSize = participants.size();
    }
    round++;

    // clean arena
    arena.restore();

    // teleport all participants to arena
    participants.forEach(it -> it.teleport(arena.getSpectatingLocation()));

    // Clear queue from previous round
    // TODO: This might be useless?
    jumpQueue.clear();

    jumpQueue.addAll(participants);
    state = ThimbleState.CHOOSING_NEXT_JUMPER;
  }

  private boolean canStartNextRound() {
    return isWaterRegionFull();
  }

  private int getAvailableBlocks() {
    AtomicInteger availableBlocksCount = new AtomicInteger();

    arena
        .getWaterRegion()
        .forEachBlock(
            arena.getJumpingLocation().getWorld(),
            block -> {
              if (block.getType() == Material.WATER
                  || block.getType() == Material.STATIONARY_WATER) {
                availableBlocksCount.incrementAndGet();
              }
            });

    return availableBlocksCount.get();
  }

  private boolean isWaterRegionFull() {
    return getAvailableBlocks() == 0;
  }

  void setState(ThimbleState state, int delay) {
    this.state = state;
    logicTask.setNextAction(delay);
  }

  void pickNextJumper() {
    if (jumpQueue.isEmpty()) {
      jumpQueue.addAll(participants);
    }

    currentJumper = jumpQueue.poll();
    currentJumper.teleport(arena.getJumpingLocation());
    state = ThimbleState.JUMPING;
  }

  void onJump(PracticePlayer jumper, boolean success, Location location) {

    if (!success) {
      if (participants.size() > 2 || getAvailableBlocks() > 1) {
        participants.remove(jumper);
        addSpectator(jumper);
      }
    } else {

      location.getBlock().setType(Material.DIRT);
    }

    if (participants.size() == 1) {
      end(participants.stream().findFirst().orElse(null));
    } else if (canStartNextRound()) {
      startNextRound();
    } else {
      state = ThimbleState.CHOOSING_NEXT_JUMPER;
      jumper.teleport(arena.getSpectatingLocation());
    }

    this.currentJumper = null;
  }

  void end(PracticePlayer winner) {
    Bukkit.broadcastMessage("Thimble winner: " + winner.getName());

    LobbyService lobbyService = Practice.getService(LobbyService.class);
    participants.forEach(lobbyService::moveToLobby);
    spectators.forEach(lobbyService::moveToLobby);
    arena.restore();
    finish();
  }

  @Override
  protected void cancel() {}

  @Override
  protected Location getSpectatingLocation() {
    return arena.getSpectatingLocation();
  }

  public boolean hasEnoughPlayers() {
    return participants.size() >= requiredParticipants;
  }

  public boolean isFull() {
    return participants.size() == 36;
  }
}
