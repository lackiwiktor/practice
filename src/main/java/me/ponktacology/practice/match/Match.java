package me.ponktacology.practice.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.match.event.MatchEndEvent;
import me.ponktacology.practice.match.event.MatchStartCountdownEvent;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.match.participant.GameParticipant;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.statistics.PlayerMatchStatistics;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.Countdown;
import me.ponktacology.practice.util.EventUtil;
import me.ponktacology.practice.util.PlayerUtil;
import me.ponktacology.practice.util.TaskDispatcher;
import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.Messenger;
import me.ponktacology.practice.util.message.Recipient;
import me.ponktacology.practice.util.visibility.VisibilityService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Match implements Recipient {

  protected final UUID id = UUID.randomUUID();
  protected final Ladder ladder;
  protected final boolean ranked;
  protected final boolean duel;
  protected final MatchArenaCopy arena;
  protected final List<UUID> spectators = Lists.newArrayList();
  protected final Set<Location> destroyableBlocks = Sets.newHashSet();
  protected final Map<UUID, Team> playerToTeamMap = Maps.newHashMap();
  protected @Nullable Team winner;
  protected List<Team> losers = Lists.newArrayList();
  protected MatchState state; // late init
  private long startedOn; // late init

  public void start() {
    if (!Bukkit.isPrimaryThread()) {
      cancel("ERROR");
      throw new IllegalStateException("Match was run off the main thread");
    }
    Practice.getService(MatchService.class).add(this);
    startedOn = System.currentTimeMillis();
    arena.setOccupied(true);
    losers.addAll(getTeams());
    if (ladder.isAllowBuild()) arena.takeSnapshot();
    state = MatchState.COUNTDOWN;
    forEachTeam(
        team -> {
          Location spawnLocation = getSpawnLocation(team);
          team.getPlayers()
              .forEach(
                  player -> {
                    Practice.getService(MatchService.class)
                        .updatePlayerMatch(player.getUuid(), this);
                    playerToTeamMap.put(player, team);

                    if (player.isOnline()) {
                      Player bukkitPlayer = player.getPlayer();
                      bukkitPlayer.teleport(spawnLocation);
                      PlayerUtil.resetPlayer(bukkitPlayer);
                      // TODO: Move to separate service
                      // player.giveKits(ladder);
                      // player.setRematchData(null);
                    }
                  });
        });

    updateVisibility(true);
    EventUtil.callEvent(new MatchStartCountdownEvent(this));

    Countdown.of(
        5,
        seconds -> {
          if (state != MatchState.COUNTDOWN) return true;
          if (seconds == 0) {
            state = MatchState.IN_PROGRESS;
            EventUtil.callEvent(new MatchStartEvent(this));
            Messenger.message(this, Messages.MATCH_START.get());
          } else Messenger.message(this, Messages.MATCH_COUNTDOWN.match("{time}", seconds));
          return false;
        });
  }

  protected abstract boolean canEndRound();

  public void endMatch(@Nullable Team winner) {
    if (this.state == MatchState.ENDING) return;
    this.winner = winner;
    this.losers.remove(winner);
    this.state = MatchState.ENDING;

    forEachTeam(
        team -> {
          for (GameParticipant player : team.getOnlinePlayers()) {
            if (player.isAlive()) {
              player.createInventorySnapshot();
            }
          }
        });

    //  inventorySnapshotTracker.commit();
    EventUtil.callEvent(new MatchEndEvent(this));
    TaskDispatcher.runLater(() -> finish(), 3L, TimeUnit.SECONDS);
  }

  public void cancel(String reason) {
    Messenger.message(this, Messages.MATCH_CANCELLED.match("{reason}", reason));
    finish();
  }

  private void finish() {
    state = MatchState.FINISHED;

    for (Player player : getOnlinePlayers()) {
      if (spectators.contains(player.getUniqueId())) {
        stopSpectating(player, /* silent mode */ true, true);
      } else {
        Practice.getService(LobbyService.class).moveToLobby(player);
        if (isParticipating(player)) {
          Practice.getService(MatchService.class).updatePlayerMatch(player, null);
        }
      }
    }

    updateVisibility(false);

    Practice.getService(MatchService.class).remove(this);

    if (ladder.isAllowBuild()) {
      arena.restore();
    }

    arena.setOccupied(false);
  }

  public void markAsDisconnected(GameParticipant disconnectedPlayer) {
    disconnectedPlayer.markAsDisconnected();

    for (Player player : getOnlinePlayers()) {
      Messenger.message(
          player,
          Messages.MATCH_PLAYER_DISCONNECTED.match(
              "{player}", getFormattedDisplayName(disconnectedPlayer, player)));
    }

    if (disconnectedPlayer.isAlive()) {
      disconnectedPlayer.createCombatLogger();
    }

    Practice.getService(MatchService.class).updatePlayerMatch(disconnectedPlayer, null);
  }

  public void markAsDead(GameParticipant deadPlayer, List<ItemStack> drops) {
    deadPlayer.createInventorySnapshot();

    deadPlayer.markAsDead();

    if (!deadPlayer.isDisconnected()) {
      Player bukkitPlayer = deadPlayer.getPlayer();
      updateVisibility(false);
      bukkitPlayer.setVelocity(new Vector());
      PlayerUtil.resetPlayer(bukkitPlayer);
    }

   GameParticipant lastAttacker = deadPlayer.getLastAttacker();

    for (GameParticipant player : getOnlinePlayers()) {
      String message;
      if (lastAttacker != null) {
        message =
            Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                new MessagePattern("{player}", getFormattedDisplayName(deadPlayer, player)),
                new MessagePattern("{killer}", getFormattedDisplayName(lastAttacker, player)));
      } else {
        message =
            Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match(
                new MessagePattern("{player}", getFormattedDisplayName(deadPlayer, player)));
      }
      Messenger.message(player, message);
    }

    // Does this do anything?
   // new DeathPackets(this, deadPlayer).sendDeathPackets();

    // Remove Kit Books
    drops.removeIf(it -> deadPlayer.getMatchingKit(ladder, it) != null);

    if (canEndRound()) {
      drops.clear();
      endMatch(getTeams().stream().filter(it -> !it.isDead()).findFirst().orElse(null));
    } else if(deadPlayer.isOnline()){
      startSpectating(deadPlayer.getPlayer(), deadPlayer, true);
    }
  }

  private void updateVisibility(boolean flicker) {
    for (Player player : getOnlinePlayers()) {
      Practice.getService(VisibilityService.class).update(player, flicker);
    }
  }

  public String getFormattedDisplayName(GameParticipant participant, Player observer) {
    ChatColor color;
    if (isParticipating(observer)) {
      Team team = getTeam(observer);
      color = team.hasPlayer(participant.getUuid()) ? ChatColor.GREEN : ChatColor.RED;
    } else {
      color = getRelativeColor(getTeam(participant));
    }

    return color + participant.getName();
  }

  public void startSpectating(Player spectator, GameParticipant other, boolean silent) {
    spectators.add(spectator.getUniqueId());
    //  spectator.setState(PlayerState.SPECTATING, new StateSpectatingData(this));
    Practice.getService(HotBarService.class).apply(spectator);
    spectator.setAllowFlight(true);
    spectator.setFlying(true);
    Player player = other.getPlayer();
    // Online check maybe?
    spectator.teleport(player.getLocation());
    updateVisibility(false);
    if (!silent)
      Messenger.message(
          this, Messages.MATCH_PLAYER_STARTED_SPECTATING.match("{player}", spectator.getName()));
  }

  public void stopSpectating(Player spectator, boolean silent, boolean moveToLobby) {
    spectators.remove(spectator);
    if (moveToLobby) Practice.getService(LobbyService.class).moveToLobby(spectator);
    if (!silent)
      Messenger.message(
          this, Messages.MATCH_PLAYER_STOPPED_SPECTATING.match("{player}", spectator.getName()));
  }

  public boolean isParticipating(GameParticipant player) {
    return getTeam(player) != null;
  }

  public boolean isParticipating(Player player) {
    return getTeam(player) != null;
  }

  public abstract List<String> getBoard(PracticePlayer player);

  public abstract Location getSpawnLocation(Team team);

  public abstract List<Team> getTeams();

  public boolean isFinished() {
    return state == MatchState.FINISHED;
  }

  public @Nullable Team getTeam(GameParticipant player) {
    return playerToTeamMap.get(player.getUuid());
  }

  public @Nullable Team getTeam(Player player) {
    return playerToTeamMap.get(player.get2UniqueId());
  }

  public boolean isOnSameTeam(GameParticipant damagedPlayer, GameParticipant damagerPlayer) {
    Team team = getTeam(damagedPlayer);
    Preconditions.checkNotNull(team, "team");
    return team.hasPlayer(damagerPlayer);
  }

  protected int getPlayersCount() {
    return getTeams().stream().mapToInt(Team::size).sum();
  }

  public boolean isOutsideArena(Location location) {
    return !arena.isIn(location);
  }

  // Might be too many iterations
  public List<Player> getOnlinePlayers() {
    List<Player> onlinePlayers = new ArrayList<>();
    onlinePlayers.addAll(
        spectators.stream()
            .map(Bukkit::getPlayer)
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
    forEachTeam(
        team ->
            onlinePlayers.addAll(
                team.getOnlinePlayers().stream()
                    .map(GameParticipant::getPlayer)
                    .collect(Collectors.toList())));
    return onlinePlayers;
  }

  @Override
  public void receive(String message) {
    // forEachTeam(team -> getOnlinePlayers(team).forEach(player -> player.receive(message)));
  }

  public void forEachTeam(Consumer<Team> consumer) {
    for (Team team : getTeams()) {
      consumer.accept(team);
    }
  }

  public abstract ChatColor getRelativeColor(Team team);

  public boolean canDestroyBlock(Block block) {
    return destroyableBlocks.contains(block.getLocation());
  }

  public void addDestroyableBlock(Block block) {
    destroyableBlocks.add(block.getLocation());
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Match match = (Match) o;
    return Objects.equals(id, match.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
