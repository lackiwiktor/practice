package country.pvp.practice.match;

import com.google.common.collect.Sets;
import country.pvp.practice.PracticePlugin;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.message.Messager;
import country.pvp.practice.message.Messages;
import country.pvp.practice.message.Recipient;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.message.component.ChatHelper;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public abstract class Match implements Recipient {

    final VisibilityUpdater visibilityUpdater;
    final LobbyService lobbyService;
    final ItemBarService itemBarService;
    final Arena arena;
    final Ladder ladder;
    final boolean ranked;
    final boolean duel;
    final Set<PlayerSession> spectators = Sets.newHashSet();
    MatchState state = MatchState.COUNTDOWN;
    private final UUID id = UUID.randomUUID(); //match-id
    private final MatchManager matchManager;
    private BukkitRunnable countDownRunnable;

    private final InventorySnapshotManager snapshotManager;
    private final Set<InventorySnapshot> snapshots = Sets.newHashSet();

    public Match(InventorySnapshotManager snapshotManager, MatchManager matchManager, VisibilityUpdater visibilityUpdater, LobbyService lobbyService, ItemBarService itemBarService, Arena arena, Ladder ladder, boolean ranked, boolean duel) {
        this.snapshotManager = snapshotManager;
        this.matchManager = matchManager;
        this.visibilityUpdater = visibilityUpdater;
        this.lobbyService = lobbyService;
        this.itemBarService = itemBarService;
        this.arena = arena;
        this.ladder = ladder;
        this.ranked = ranked;
        this.duel = duel;
    }

    @Nullable Team winner;

    public void init() {
        matchManager.add(this);
        TaskDispatcher.runLater(this::start, 100L, TimeUnit.MILLISECONDS);
    }

    private void start() {
        prepareTeams();
        startCountDown();
    }

    void prepareTeam(Team team, Location spawnLocation) {
        team.createMatchSession(this);
        team.reset();
        team.giveKits(ladder);
        team.teleport(spawnLocation);
        team.clearRematchData();
    }

    abstract void prepareTeams();

    void end(@Nullable Team winner) {
        this.state = MatchState.END;
        this.winner = winner;

        cancelCountDown();
        createInventorySnapshots();
        onPreEnd();
        snapshotManager.addAll(snapshots);
        sendResultComponent();

        Runnable runnable = () -> {
            movePlayersToLobby();
            moveSpectatorsToLobby();
            finish();
        };

        TaskDispatcher.runLater(runnable, 3500L, TimeUnit.MILLISECONDS);
    }

    abstract void onPreEnd();

    private void finish() {
        matchManager.remove(this);
    }

    void startCountDown() {
        AtomicInteger count = new AtomicInteger(6);
        (countDownRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (count.decrementAndGet() > 0) {
                    broadcast(Messages.MATCH_COUNTDOWN.match("{time}", count.get()));
                } else {
                    state = MatchState.FIGHT;
                    broadcast(Messages.MATCH_START);
                    cancel();
                }
            }
        }).runTaskTimer(PracticePlugin.getPlugin(PracticePlugin.class), 20L, 20L);
    }

    void cancelCountDown() {
        if (countDownRunnable == null) return;
        countDownRunnable.cancel();
    }

    abstract void movePlayersToLobby();

    private void moveSpectatorsToLobby() {
        for (PlayerSession spectator : spectators) {
            stopSpectating(spectator, false);
        }
    }

    public void handleDeath(PlayerSession player) {
        createInventorySnapshot(player);
        player.setDead(true);
        updateTeamVisibility();
        broadcastPlayerDeath(player);
        PlayerUtil.resetPlayer(player.getPlayer());
        player.setVelocity(new Vector());
        player.teleport(player.getLocation().add(0, 3, 0));
        handleRespawn(player);
    }


    abstract void broadcastPlayerDeath(PlayerSession player);

    abstract void handleRespawn(PlayerSession player);

    public abstract void handleDisconnect(PlayerSession player);

    abstract void updateTeamVisibility();

    void broadcast(String message) {
        Messager.message(this, message);
    }

    void broadcast(Messages message) {
        Messager.message(this, message);
    }

    void broadcast(country.pvp.practice.match.team.Team team, String message) {
        Messager.message(team, message);
    }

    List<PlayerSession> getOnlinePlayers() {
        return new ArrayList<>(spectators);
    }

    public void cancel(String reason) {
        broadcast(Messages.MATCH_CANCELLED.match("{reason}", reason));
        end(null);
    }

    String getFormattedDisplayName(PlayerSession player, country.pvp.practice.match.team.Team team) {
        return (team.hasPlayer(player) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
    }

    public void startSpectating(PlayerSession spectator, PlayerSession player) {
        spectator.setState(PlayerState.SPECTATING, new SessionSpectatingData(this));
        spectators.add(spectator);
        itemBarService.apply(spectator);
        broadcast(Messages.MATCH_PLAYER_STARTED_SPECTATING.match("{player}", spectator.getName()));
        spectator.teleport(player.getLocation());
        setupSpectator(spectator);

        for (PlayerSession matchPlayer : getOnlinePlayers()) {
            visibilityUpdater.update(spectator, matchPlayer);
            visibilityUpdater.update(matchPlayer, spectator);
        }
    }

    void setupSpectator(PlayerSession player) {
        player.enableFlying();
    }

    public void stopSpectating(PlayerSession spectator, boolean broadcast) {
        if (broadcast)
            broadcast(Messages.MATCH_PLAYER_STOPPED_SPECTATING.match("{player}", spectator.getName()));
        spectators.remove(spectator);
        lobbyService.moveToLobby(spectator);
    }

    public boolean isBuild() {
        return ladder.isBuild();
    }

    abstract void createInventorySnapshots();

    InventorySnapshot createInventorySnapshot(PlayerSession session) {
        InventorySnapshot snapshot = InventorySnapshot.create(session);
        snapshots.add(snapshot);
        return snapshot;
    }

    abstract void sendResultComponent();

    BaseComponent[] createComponent(PlayerSession player) {
        return new ChatComponentBuilder(ChatColor.YELLOW + player.getName())
                .attachToEachPart(
                        ChatHelper.hover(ChatColor.GREEN.toString().concat("Click to view inventory of ").concat(ChatColor.GOLD.toString()).concat(player.getName())))
                .attachToEachPart(
                        ChatHelper.click("/viewinv ".concat(player.getUuid().toString())))
                .create();
    }

    public abstract boolean areOnTheSameTeam(PlayerSession damagedPlayer, PlayerSession damagerPlayer);

    public abstract boolean isInMatch(PlayerSession player);

    public abstract boolean isAlive(PlayerSession player);

    abstract int getPlayersCount();

    public abstract List<String> getBoard(PlayerSession player);

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
