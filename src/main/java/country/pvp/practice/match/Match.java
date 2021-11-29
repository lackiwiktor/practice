package country.pvp.practice.match;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import country.pvp.practice.PracticePlugin;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.concurrent.TaskDispatcher;
import country.pvp.practice.itembar.ItemBarManager;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.elo.EloUtil;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.message.*;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.message.component.ChatHelper;
import country.pvp.practice.player.PlayerUtil;
import country.pvp.practice.player.PracticePlayer;
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
public class Match<T extends Team> implements Recipient {

    final VisibilityUpdater visibilityUpdater;
    final LobbyService lobbyService;
    final MatchManager matchManager;
    final ItemBarManager itemBarManager;
    final InventorySnapshotManager snapshotManager;
    final UUID id = UUID.randomUUID(); //match-id
    final Ladder ladder;
    final Arena arena;
    final T teamA;
    final T teamB;
    final boolean ranked;
    final boolean duel;
    final Set<PracticePlayer> spectators = Sets.newHashSet();
    final Map<PracticePlayer, InventorySnapshot> snapshots = Maps.newHashMap();

    private @Nullable T winner;

    private MatchState state = MatchState.COUNTDOWN;
    private BukkitRunnable countDownRunnable;

    public void start() {
        matchManager.add(this);
        prepareTeams();
    }

    void prepareTeams() {
        prepareTeam(teamA, arena.getSpawnLocation1());
        prepareTeam(teamB, arena.getSpawnLocation2());
        updateVisibility();
        startCountDown();
    }

    void prepareTeam(T team, Location spawnLocation) {
        team.setMatchData(this);
        team.setPlayersState(PlayerState.IN_MATCH);
        team.teleport(spawnLocation);
        team.resetPlayers();
        team.giveKits(ladder);
    }

    void updateVisibility() {
        for (PracticePlayer playerA : getAllOnlinePlayers()) {
            for (PracticePlayer playerB : getAllOnlinePlayers()) {
                visibilityUpdater.update(playerA, playerB);
                visibilityUpdater.update(playerB, playerA);
            }
        }
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

    void broadcast(country.pvp.practice.match.team.Team team, Messages message) {
        Messager.message(team, message);
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

    public T getOpponent(PracticePlayer player) {
        return teamA.hasPlayer(player) ? teamB : teamA;
    }

    public T getTeam(PracticePlayer player) {
        return teamA.hasPlayer(player) ? teamA : teamB;
    }

    public boolean isInMatch(PracticePlayer player) {
        return teamA.hasPlayer(player) || teamB.hasPlayer(player);
    }

    public boolean isAlive(PracticePlayer player) {
        return getTeam(player).isAlive(player);
    }

    public void handleDeath(PracticePlayer player) {
        createInventorySnapshot(player);
        PlayerMatchData matchData = player.getStateData();
        matchData.setDead(true);

        if (matchData.getLastAttacker() != null) {
            PracticePlayer killer = matchData.getLastAttacker();

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

        updateVisibility();
        PlayerUtil.resetPlayer(player.getPlayer());
        player.setVelocity(new Vector());
        player.teleport(player.getLocation().add(0, 3, 0));

        handleRespawn(player);
    }

    void handleRespawn(PracticePlayer player) {
        country.pvp.practice.match.team.Team team = getTeam(player);

        if (team.isDead()) {
            end(getOpponent(team));
            return;
        }

        setupSpectator(player);
    }

    public void handleDisconnect(PracticePlayer player) {
        createInventorySnapshot(player);
        broadcast(teamA, Messages.MATCH_PLAYER_DISCONNECT.match("{player}", getFormattedDisplayName(player, teamA)));
        broadcast(teamB, Messages.MATCH_PLAYER_DISCONNECT.match("{player}", getFormattedDisplayName(player, teamB)));

        PlayerMatchData matchData = player.getStateData();

        matchData.setDead(true);
        matchData.setDisconnected(true);

        country.pvp.practice.match.team.Team team = getTeam(player);

        if (team.isDead() && state != MatchState.END) {
            end(getOpponent(team));
        }
    }

    void setupSpectator(PracticePlayer player) {
        player.enableFlying();
    }

    void end(@Nullable T winner) {
        state = MatchState.END;
        this.winner = winner;
        cancelCountDown();

        for (PracticePlayer player : getAllAlivePlayers()) {
            createInventorySnapshot(player);
        }

        if (winner != null) {
            T loser = getOpponent(winner);

            if (ranked) {
                int winnerNewRating = EloUtil.getNewRating(winner.getElo(ladder), loser.getElo(ladder), true);
                int loserNewRating = EloUtil.getNewRating(loser.getElo(ladder), winner.getElo(ladder), false);

                loser.setElo(ladder, loserNewRating);
                winner.setElo(ladder, winnerNewRating);
            }

            BaseComponent[] components = createFinalComponent(winner, loser);

            for (PracticePlayer player : getAllOnlinePlayersIncludingSpectators()) {
                player.sendComponent(components);
            }
        }

        Runnable runnable = () -> {
            movePlayersToLobby();
            matchManager.remove(this);
        };

        TaskDispatcher.runLater(runnable, 5L, TimeUnit.SECONDS);
    }

    void movePlayersToLobby() {
        for (PracticePlayer player : getAllOnlinePlayers()) {
            lobbyService.moveToLobby(player);
        }

        for (PracticePlayer spectator : spectators) {
            stopSpectating(spectator, false);
        }
    }

    public void cancel(String reason) {
        broadcast(Messages.MATCH_CANCELLED.match("{reason}", reason));
        end(null);
    }

    public String getFormattedDisplayName(PracticePlayer player, country.pvp.practice.match.team.Team team) {
        return (team.hasPlayer(player) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
    }

    public String getFormattedDisplayName(PracticePlayer player, PracticePlayer other) {
        return getFormattedDisplayName(player, getTeam(other));
    }

    public void startSpectating(PracticePlayer spectator, PracticePlayer player) {
        spectator.setState(PlayerState.SPECTATING, new PlayerSpectatingData(this));
        spectators.add(spectator);
        itemBarManager.apply(spectator);
        broadcast(Messages.MATCH_PLAYER_STARTED_SPECTATING.match("{player}", spectator.getName()));
        spectator.teleport(player.getLocation());
        setupSpectator(spectator);

        for (PracticePlayer matchPlayer : getAllOnlinePlayersIncludingSpectators()) {
            visibilityUpdater.update(spectator, matchPlayer);
            visibilityUpdater.update(matchPlayer, spectator);
        }
    }

    void createInventorySnapshot(PracticePlayer player) {
        InventorySnapshot snapshot = InventorySnapshot.create(player);

        country.pvp.practice.match.team.Team team = getTeam(player);
        if (team.size() == 1) {
            country.pvp.practice.match.team.Team opponent = getOpponent(team);
            PracticePlayer opponentPlayer = opponent.getPlayers().stream().findAny().orElse(null);
            snapshot.setOpponent(opponentPlayer == null ? null : opponentPlayer.getUuid());
        }

        snapshotManager.add(snapshot);
    }

    public void stopSpectating(PracticePlayer spectator, boolean broadcast) {
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

    public Set<PracticePlayer> getAllOnlinePlayers() {
        Set<PracticePlayer> players = new HashSet<>(teamA.getOnlinePlayers());
        players.addAll(teamB.getOnlinePlayers());
        return players;
    }

    public Set<PracticePlayer> getAllAlivePlayers() {
        Set<PracticePlayer> players = new HashSet<>(teamA.getAlivePlayers());
        players.addAll(teamB.getAlivePlayers());
        return players;
    }

    public Set<PracticePlayer> getAllPlayers() {
        Set<PracticePlayer> players = new HashSet<>(teamA.getPlayers());
        players.addAll(teamB.getPlayers());
        return players;
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

    public BaseComponent[] createComponent(T team, boolean winner) {
        ChatComponentBuilder builder = new ChatComponentBuilder(winner ? ChatColor.GREEN + "Winner: " : ChatColor.RED + "Loser: ");

        for (PracticePlayer player : team.getPlayers()) {
            builder.append(createComponent(player));
        }

        return builder.create();
    }

    public BaseComponent[] createComponent(PracticePlayer player) {
        return new ChatComponentBuilder(ChatColor.YELLOW + player.getName())
                .attachToEachPart(
                        ChatHelper.hover(ChatColor.GREEN.toString().concat("Click to view inventory of ").concat(ChatColor.GOLD.toString()).concat(player.getName())))
                .attachToEachPart(
                        ChatHelper.click("/viewinv ".concat(player.getUuid().toString())))
                .create();
    }

    public Set<PracticePlayer> getAllOnlinePlayersIncludingSpectators() {
        Set<PracticePlayer> players = getAllOnlinePlayers();
        players.addAll(spectators);
        return players;
    }

    public boolean isBuild() {
        return ladder.isBuild();
    }
}
