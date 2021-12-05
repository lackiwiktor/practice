package country.pvp.practice.match;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import country.pvp.practice.PracticePlugin;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.message.*;
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
public abstract class Match<T extends Team> implements Recipient {

    final VisibilityUpdater visibilityUpdater;
    final LobbyService lobbyService;
    private final MatchManager matchManager;
    final ItemBarService itemBarService;
    private final InventorySnapshotManager snapshotManager;
    private final UUID id = UUID.randomUUID(); //match-id
    final Ladder ladder;
    private final Arena arena;
    final T teamA;
    final T teamB;
    final boolean ranked;
    final boolean duel;
    final Set<PlayerSession> spectators = Sets.newHashSet();
    private final Map<PlayerSession, InventorySnapshot> snapshots = Maps.newHashMap();

    @Nullable T winner;

    MatchState state = MatchState.COUNTDOWN;
    private BukkitRunnable countDownRunnable;

    public void init() {
        matchManager.add(this);
        TaskDispatcher.runLater(this::start, 100L, TimeUnit.MILLISECONDS);
    }

    private void start() {
        prepareTeams();
        startCountDown();
    }

    void prepareTeams() {
        prepareTeam(teamA, arena.getSpawnLocation1());
        prepareTeam(teamB, arena.getSpawnLocation2());
        updateTeamVisibility();
    }

    void prepareTeam(Team team, Location spawnLocation) {
        team.createMatchSession(this);
        team.reset();
        team.giveKits(ladder);
        team.teleport(spawnLocation);
        team.clearRematchData();
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

    void broadcast(String message) {
        Messager.message(this, message);
    }

    void broadcast(Messages message) {
        Messager.message(this, message);
    }

    void broadcast(country.pvp.practice.match.team.Team team, String message) {
        Messager.message(team, message);
    }

    void cancelCountDown() {
        if (countDownRunnable == null) return;
        countDownRunnable.cancel();
    }

    public T getOpponent(country.pvp.practice.match.team.Team team) {
        return team.equals(teamA) ? teamB : teamA;
    }

    public T getOpponent(PlayerSession player) {
        return teamA.hasPlayer(player) ? teamB : teamA;
    }

    public T getTeam(PlayerSession player) {
        return teamA.hasPlayer(player) ? teamA : teamB;
    }

    public boolean isInMatch(PlayerSession player) {
        return teamA.hasPlayer(player) || teamB.hasPlayer(player);
    }

    public boolean isAlive(PlayerSession player) {
        return getTeam(player).isAlive(player);
    }

    public void handleDeath(PlayerSession player) {
        createInventorySnapshot(player);
        player.setDead(true);

        if (player.hasLastAttacker()) {
            PlayerSession killer = player.getLastAttacker();

            broadcast(teamA,
                    Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                            new MessagePattern("{player}", getFormattedDisplayName(player, teamA)),
                            new MessagePattern("{killer}", getFormattedDisplayName(killer, teamA))));
            broadcast(teamB,
                    Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                            new MessagePattern("{player}", getFormattedDisplayName(player, teamB)),
                            new MessagePattern("{killer}", getFormattedDisplayName(killer, teamB))));
        } else {
            broadcast(teamA,
                    Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match("{player}", getFormattedDisplayName(player, teamA)));
            broadcast(teamB,
                    Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match("{player}", getFormattedDisplayName(player, teamB)));
        }

        updateTeamVisibility();
        PlayerUtil.resetPlayer(player.getPlayer());
        player.setVelocity(new Vector());
        player.teleport(player.getLocation().add(0, 3, 0));

        handleRespawn(player);
    }

    void handleRespawn(PlayerSession player) {
        Team team = getTeam(player);

        if (team.isDead()) {
            end(getOpponent(team));
            return;
        }

        setupSpectator(player);
    }

    public void handleDisconnect(PlayerSession player) {
        createInventorySnapshot(player);
        broadcast(teamA, Messages.MATCH_PLAYER_DISCONNECT.match("{player}", getFormattedDisplayName(player, teamA)));
        broadcast(teamB, Messages.MATCH_PLAYER_DISCONNECT.match("{player}", getFormattedDisplayName(player, teamB)));
        player.handleDisconnectInMatch();
        country.pvp.practice.match.team.Team team = getTeam(player);

        if (team.isDead() && state != MatchState.END) {
            end(getOpponent(team));
        }
    }

    void setupSpectator(PlayerSession player) {
        player.enableFlying();
    }

    void end(@Nullable T winner) {
        state = MatchState.END;
        this.winner = winner;
        cancelCountDown();

        createInventorySnapshots();

        if (winner != null) {
            T loser = getOpponent(winner);
            BaseComponent[] components = createFinalComponent(winner, loser);


            for (PlayerSession player : getOnlinePlayers()) {
                player.sendComponent(components);
            }
        }

        Runnable runnable = () -> {
            movePlayersToLobby();
            moveSpectatorsToLobby();
            clear();
        };

        TaskDispatcher.runLater(runnable, 3500L, TimeUnit.MILLISECONDS);
    }

    public void cancel(String reason) {
        broadcast(Messages.MATCH_CANCELLED.match("{reason}", reason));
        end(null);
    }

    public String getFormattedDisplayName(PlayerSession player, country.pvp.practice.match.team.Team team) {
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

    void createInventorySnapshot(PlayerSession player) {
        InventorySnapshot snapshot = InventorySnapshot.create(player);

        Team team = getTeam(player);
        Team opponent = getOpponent(team);

        if (opponent instanceof SoloTeam) {
            SoloTeam soloOpponent = (SoloTeam) opponent;
            PlayerSession opponentPlayer = soloOpponent.getPlayerSession();
            snapshot.setOpponent(opponentPlayer.getUuid());
        }

        snapshotManager.add(snapshot);
    }

    public void stopSpectating(PlayerSession spectator, boolean broadcast) {
        if (broadcast) broadcast(Messages.MATCH_PLAYER_STOPPED_SPECTATING.match("{player}", spectator.getName()));
        spectators.remove(spectator);
        lobbyService.moveToLobby(spectator);
    }

    @Override
    public void receive(String message) {
        teamA.receive(message);
        teamB.receive(message);
    }

    public int getPlayersCount() {
        return teamA.size() + teamB.size();
    }

    public String getTeamADisplayName() {
        return teamA.getName();
    }

    public String getTeamBDisplayName() {
        return teamB.getName();
    }

    public Optional<T> getWinner() {
        return Optional.ofNullable(winner);
    }

    public BaseComponent[] createFinalComponent(T winner, T loser) {
        BaseComponent[] winnerComponent = createComponent(winner, true);
        BaseComponent[] loserComponent = createComponent(loser, false);

        ChatComponentBuilder builder = new ChatComponentBuilder("");
        builder.append(Bars.CHAT_BAR);
        builder.append("\n");
        builder.append(ChatColor.GOLD.toString().concat("Post-Match Inventories ").concat(ChatColor.GRAY.toString()).concat("(click name to view)"));
        builder.append("\n");
        builder.append(winnerComponent);
        builder.append(ChatColor.GRAY + " - ");
        builder.append(loserComponent);
        builder.append("\n");
        builder.append(Bars.CHAT_BAR);

        return builder.create();
    }


    public BaseComponent[] createComponent(PlayerSession player) {
        return new ChatComponentBuilder(ChatColor.YELLOW + player.getName())
                .attachToEachPart(
                        ChatHelper.hover(ChatColor.GREEN.toString().concat("Click to view inventory of ").concat(ChatColor.GOLD.toString()).concat(player.getName())))
                .attachToEachPart(
                        ChatHelper.click("/viewinv ".concat(player.getUuid().toString())))
                .create();
    }

    public boolean isBuild() {
        return ladder.isBuild();
    }

    void moveSpectatorsToLobby() {
        for (PlayerSession spectator : spectators) {
            stopSpectating(spectator, false);
        }
    }

    void clear() {
        matchManager.remove(this);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match<?> match = (Match<?>) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract List<String> getBoard(PlayerSession session);

    abstract BaseComponent[] createComponent(T team, boolean winner);

    abstract Set<PlayerSession> getOnlinePlayers();

    abstract void movePlayersToLobby();

    abstract void createInventorySnapshots();

    abstract void updateTeamVisibility();
}
