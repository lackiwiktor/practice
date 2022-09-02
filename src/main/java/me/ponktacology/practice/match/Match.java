package me.ponktacology.practice.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.ponktacology.practice.Messages;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.ladder.LadderType;
import me.ponktacology.practice.lobby.LobbyService;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.pearl_cooldown.PearlCooldownTracker;
import me.ponktacology.practice.match.procedure.PlayerDeathProcedure;
import me.ponktacology.practice.match.procedure.PlayerDisconnectProcedure;
import me.ponktacology.practice.match.snapshot.InventorySnapshot;
import me.ponktacology.practice.match.statistics.PlayerMatchStatistics;
import me.ponktacology.practice.match.statistics.PlayerStatisticsTracker;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.TaskDispatcher;
import me.ponktacology.practice.util.message.Bars;
import me.ponktacology.practice.util.message.Messenger;
import me.ponktacology.practice.util.message.Recipient;
import me.ponktacology.practice.util.visibility.VisibilityService;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Match implements Recipient {

  private final UUID id = UUID.randomUUID();
  private final Ladder ladder;
  private final boolean ranked;
  private final boolean duel;
  private final MatchArenaCopy arena;
  private final List<PracticePlayer> spectators = Lists.newArrayList();
  private final Set<Location> destroyableBlocks = Sets.newHashSet();
  private final PlayerStatisticsTracker statisticsTracker = new PlayerStatisticsTracker(this);
  private final PearlCooldownTracker cooldownTracker = new PearlCooldownTracker(this);
  private final PlayerInfoTracker infoTracker = new PlayerInfoTracker(this);
  private final InventorySnapshotTracker inventorySnapshotTracker = new InventorySnapshotTracker();
  private final MatchLogicTask logicTask = new MatchLogicTask(this);

  private @Nullable Team winner;
  private MatchState state; // late init
  private long startedOn; // late init
  private boolean finished;

  public void init() {
    Practice.getService(MatchService.class).add(this);

    arena.setOccupied(true);

    if (ladder.isBuild()) {
      arena.takeSnapshot();
    }

    setState(MatchState.STARTING);
    TaskDispatcher.scheduleSync(logicTask, 1L, TimeUnit.SECONDS);
  }

  void onMatchStart() {
    statisticsTracker.clear();
    inventorySnapshotTracker.clear();
    forEachTeam(
        team -> {
          Location spawnLocation = getSpawnLocation(team);
          team.reset();
          team.giveKits(ladder);
          team.teleport(
              spawnLocation.getBlock().getType() == Material.AIR
                  ? spawnLocation
                  : spawnLocation.add(0, 2, 0));
          team.createMatchSession(this);
        });
    updateVisibility(true);
    startedOn = System.currentTimeMillis();
  }

  protected void onRoundEnd() {
    forEachTeam(
        team -> {
          for (PracticePlayer player : team.getOnlinePlayers()) {
            if (infoTracker.isAlive(player)) {
              createInventorySnapshot(player);
            }
          }
        });

    winner =
        getTeams().stream()
            .filter(it -> !isTeamDead(it))
            .findFirst()
            .orElseThrow(IllegalStateException::new);
    inventorySnapshotTracker.commit();

    Team[] losers = getLosers();
    Map<PracticePlayer, InventorySnapshot> cachedSnapshots = inventorySnapshotTracker.get();

    MatchResults matchResults = new MatchResults(winner, losers, cachedSnapshots, spectators);

    Component components = matchResults.createMatchResultMessage();

    for (PracticePlayer player : getAllOnlinePlayers()) {
      Messenger.message(player, Bars.CHAT_BAR);
      Practice.getPractice().getAudience().player(player.getPlayer()).sendMessage(components);
      Messenger.message(player, Bars.CHAT_BAR);
    }
  }

  public InventorySnapshot createInventorySnapshot(PracticePlayer player) {
    PlayerMatchStatistics statistics = statisticsTracker.getStatistics(player);
    return inventorySnapshotTracker.createInventorySnapshot(player, statistics);
  }

  public abstract boolean canEndRound();

  void onMatchEnd() {
    for (PracticePlayer session : getOnlinePlayers()) {
      Practice.getService(LobbyService.class).moveToLobby(session);
    }
    for (PracticePlayer spectator : spectators) {
      stopSpectating(spectator, false, true);
    }
    Practice.getService(MatchService.class).remove(this);

    if (ladder.isBuild()) {
      arena.restore();
    }

    arena.setOccupied(false);
    finished = true;
  }

  public void cancel(String reason) {
    Messenger.message(this, Messages.MATCH_CANCELLED.match("{reason}", reason));
    setState(MatchState.FINISHED);
  }

  public void setState(MatchState state, int delay) {
    this.state = state;
    logicTask.setNextAction(delay);
  }

  public void setState(MatchState state) {
    this.state = state;
    logicTask.setNextAction(1);
    logicTask.run();
  }

  public Team[] getLosers() {
    Team winner = getWinner();
    Set<Team> teams = new HashSet<>(getTeams());
    teams.remove(winner);
    return teams.toArray(new Team[0]);
  }

  public void onPlayerDisconnect(PracticePlayer disconnectedPlayer) {
    new PlayerDisconnectProcedure(this, disconnectedPlayer);
  }

  public void onPlayerDeath(PracticePlayer deadPlayer, List<ItemStack> drops) {
    new PlayerDeathProcedure(this, deadPlayer, drops);
  }

  public void updateVisibility(boolean flicker) {
    for (PracticePlayer session : getAllOnlinePlayers()) {
      Practice.getService(VisibilityService.class).update(session, flicker);
    }
  }

  public String getFormattedDisplayName(PracticePlayer player, Team team) {
    return (team.hasPlayer(player) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
  }

  public void startSpectating(PracticePlayer spectator, PracticePlayer other) {
    spectators.add(spectator);
    Messenger.message(
        this, Messages.MATCH_PLAYER_STARTED_SPECTATING.match("{player}", spectator.getName()));
    spectator.setState(PlayerState.SPECTATING, new StateSpectatingData(this));
    spectator.enableFlying();
    Practice.getService(HotBarService.class).apply(spectator);
    spectator.teleport(other.getLocation());
    updateVisibility(false);
  }

  public void stopSpectating(PracticePlayer spectator, boolean broadcast, boolean moveToLobby) {
    spectators.remove(spectator);

    if (broadcast) {
      Messenger.message(
          this, Messages.MATCH_PLAYER_STOPPED_SPECTATING.match("{player}", spectator.getName()));
    }

    if (moveToLobby) Practice.getService(LobbyService.class).moveToLobby(spectator);
  }

  public boolean isInMatch(PracticePlayer player) {
    return getTeam(player) != null;
  }

  public abstract List<String> getBoard(PracticePlayer player);

  protected abstract Location getSpawnLocation(Team team);

  public abstract List<Team> getTeams();

  public @Nullable Team getTeam(PracticePlayer player) {
    return getTeams().stream().filter(it -> it.hasPlayer(player)).findFirst().orElse(null);
  }

  public boolean isOnSameTeam(PracticePlayer damagedPlayer, PracticePlayer damagerPlayer) {
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

  public boolean isBuild() {
    return ladder.isBuild();
  }

  public LadderType getLadderType() {
    return ladder.getType();
  }

  protected List<PracticePlayer> getAllOnlinePlayers() {
    List<PracticePlayer> players = Lists.newArrayList(getOnlinePlayers());
    players.addAll(spectators);
    return players;
  }

  private List<PracticePlayer> getOnlinePlayers() {
    List<PracticePlayer> onlinePlayers = new ArrayList<>();
    forEachTeam(team -> onlinePlayers.addAll(team.getOnlinePlayers()));
    return onlinePlayers;
  }

  protected boolean isTeamDead(Team team) {
    return team.getOnlinePlayers().stream().noneMatch(infoTracker::isAlive);
  }

  protected int getAlivePlayersCount(Team team) {
    return (int) team.getOnlinePlayers().stream().filter(infoTracker::isAlive).count();
  }

  @Override
  public void receive(String message) {
    forEachTeam(team -> team.receive(message));
  }

  private void forEachTeam(Consumer<Team> consumer) {
    for (Team team : getTeams()) {
      consumer.accept(team);
    }
  }

  public boolean canDestroyBlock(Block block) {
    return destroyableBlocks.contains(block.getLocation());
  }

  public void addDestroyableBlock(Block block) {
    destroyableBlocks.add(block.getLocation());
  }

  public boolean isHungerDecay() {
    return ladder.getType().isHungerDecay();
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
