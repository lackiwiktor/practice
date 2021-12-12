package country.pvp.practice.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public abstract class Match implements Recipient {

    protected final VisibilityUpdater visibilityUpdater;
    protected final LobbyService lobbyService;
    protected final ItemBarService itemBarService;
    protected final Arena arena;
    protected final Ladder ladder;
    protected final boolean ranked;
    protected final boolean duel;
    protected final Set<PlayerSession> spectators = Sets.newHashSet();
    protected final Map<PlayerSession, InventorySnapshot> snapshots = Maps.newHashMap();
    protected MatchState state = MatchState.COUNTDOWN;
    protected @Nullable Team winner;
    private final UUID id = UUID.randomUUID();
    private final MatchManager matchManager;
    private final InventorySnapshotManager snapshotManager;
    private BukkitRunnable countDownRunnable;

    protected Match(InventorySnapshotManager snapshotManager, MatchManager matchManager, VisibilityUpdater visibilityUpdater, LobbyService lobbyService, ItemBarService itemBarService, Arena arena, Ladder ladder, boolean ranked, boolean duel) {
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

    public void init() {
        matchManager.add(this);
        TaskDispatcher.runLater(this::start, 100L, TimeUnit.MILLISECONDS);
    }

    private void start() {
        prepareTeams();
        startCountDown();
    }

    protected void prepareTeam(Team team, Location spawnLocation) {
        team.createMatchSession(this);
        team.reset();
        team.giveKits(ladder);
        team.teleport(
                spawnLocation.getBlock().getType() == Material.AIR ?
                        spawnLocation : spawnLocation.add(0, 2, 0));
        team.clearRematchData();
    }

    protected abstract void prepareTeams();

    protected void end(@Nullable Team winner) {
        this.state = MatchState.END;
        this.winner = winner;

        cancelCountDown();
        createInventorySnapshots();
        snapshots.values().forEach(it -> it.setCreatedAt(System.currentTimeMillis()));
        snapshotManager.addAll(snapshots.values());
        handleEnd();
        sendResultComponent();

        Runnable runnable = () -> {
            movePlayersToLobby();
            moveSpectatorsToLobby();
            finish();
        };

        TaskDispatcher.runLater(runnable, 3500L, TimeUnit.MILLISECONDS);
    }

    protected void moveTeamToLobby(Team team) {
        for (PlayerSession session : team.getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }
    }

    protected abstract void handleEnd();

    private void finish() {
        matchManager.remove(this);
    }

    public void cancel(String reason) {
        broadcast(Messages.MATCH_CANCELLED.match("{reason}", reason));
        end(null);
    }

    protected void startCountDown() {
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

    protected void cancelCountDown() {
        if (countDownRunnable == null) return;
        countDownRunnable.cancel();
    }

    protected abstract void movePlayersToLobby();

    private void moveSpectatorsToLobby() {
        for (PlayerSession spectator : spectators) {
            stopSpectating(spectator, false);
        }
    }

    public void handleDeath(PlayerSession deadPlayer) {
        createInventorySnapshot(deadPlayer);
        deadPlayer.setDead(true);
        broadcastPlayerDeath(deadPlayer);
        sendDeathPackets(deadPlayer);
        updateVisibility();
        PlayerUtil.resetPlayer(deadPlayer.getPlayer());
        deadPlayer.setVelocity(new Vector());
        deadPlayer.enableFlying();
        tryEndingMatch(deadPlayer);
    }

    private void sendDeathPackets(PlayerSession deadPlayer) {
        Location location = deadPlayer.getLocation();

        PacketPlayOutSpawnEntityWeather lightningPacket = new PacketPlayOutSpawnEntityWeather(new EntityLightning(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ(), true));
        for (PlayerSession playerSession : getAllOnlinePlayers()) {
            Player player = playerSession.getPlayer();
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(lightningPacket);
        }
    }

    protected abstract void broadcastPlayerDeath(PlayerSession player);

    protected abstract void tryEndingMatch(PlayerSession player);

    public abstract void handleDisconnect(PlayerSession player);

    protected void updateVisibility() {
        for (PlayerSession session : getOnlinePlayers()) {
            for (PlayerSession other : getOnlinePlayers()) {
                visibilityUpdater.update(session, other);
                visibilityUpdater.update(other, session);
            }
        }
    }

    protected void broadcast(String message) {
        Messager.message(this, message);
    }

    protected void broadcast(Messages message) {
        Messager.message(this, message);
    }

    protected void broadcast(country.pvp.practice.match.team.Team team, String message) {
        Messager.message(team, message);
    }

    /**
     * Returns the list of currently online players including spectators
     *
     * @return list of online players
     */
    protected List<PlayerSession> getAllOnlinePlayers() {
        List<PlayerSession> players = Lists.newArrayList(getOnlinePlayers());
        players.addAll(spectators);
        return players;
    }

    /**
     * Returns the list of currently online players, excluding spectators
     *
     * @return list of online players
     */
    protected abstract List<PlayerSession> getOnlinePlayers();

    protected String getFormattedDisplayName(PlayerSession player, country.pvp.practice.match.team.Team team) {
        return (team.hasPlayer(player) ? ChatColor.GREEN : ChatColor.RED) + player.getName();
    }

    public void startSpectating(PlayerSession spectator, PlayerSession player) {
        spectators.add(spectator);
        broadcast(Messages.MATCH_PLAYER_STARTED_SPECTATING.match("{player}", spectator.getName()));
        spectator.setState(PlayerState.SPECTATING, new SessionSpectatingData(this));
        setupSpectator(spectator, player);
    }

    private void setupSpectator(PlayerSession spectator, PlayerSession other) {
        spectator.enableFlying();
        itemBarService.apply(spectator);
        spectator.teleport(other.getLocation());
        for (PlayerSession matchPlayer : getAllOnlinePlayers()) {
            visibilityUpdater.update(spectator, matchPlayer);
            visibilityUpdater.update(matchPlayer, spectator);
        }
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

    protected abstract void createInventorySnapshots();

    protected InventorySnapshot createInventorySnapshot(PlayerSession session) {
        InventorySnapshot snapshot = InventorySnapshot.create(session);
        snapshots.put(session, snapshot);
        return snapshot;
    }

    private void sendResultComponent() {
        BaseComponent[] components = createMatchResultMessage(winner, getLosers());

        for (PlayerSession player : getAllOnlinePlayers()) {
            Messager.message(player, Bars.CHAT_BAR);
            player.sendComponent(components);
            Messager.message(player, Bars.CHAT_BAR);
        }
    }

    private BaseComponent[] createMatchResultMessage(Team winner, Team... losers) {
        ChatComponentBuilder builder = new ChatComponentBuilder("");
        BaseComponent[] winnerComponent = new ChatComponentBuilder(Messages.MATCH_RESULT_OVERVIEW_WINNER.get())
                .append(createTeamSnapshotMessage(winner))
                .create();
        ChatComponentBuilder loserComponentBuilder = new ChatComponentBuilder(Messages.MATCH_RESULT_OVERVIEW_LOSER.get());

        for (int i = 0; i < losers.length; i++) {
            loserComponentBuilder.append(createTeamSnapshotMessage(losers[i]));
            if (i < losers.length - 1) {
                loserComponentBuilder.append(MessageUtil.color("&7, "));
                loserComponentBuilder.getCurrent().setClickEvent(null);
                loserComponentBuilder.getCurrent().setHoverEvent(null);
            }
        }

        BaseComponent[] loserComponent = loserComponentBuilder.create();

        builder.append(winnerComponent);
        builder.append(Messages.MATCH_RESULT_OVERVIEW_SPLITTER.get());
        builder.append(loserComponent);

        return builder.create();
    }

    private BaseComponent[] createTeamSnapshotMessage(Team team) {
        ChatComponentBuilder builder = new ChatComponentBuilder("");
        List<PlayerSession> players = team.getPlayers();

        for (int i = 0; i < team.size(); i++) {
            PlayerSession player = players.get(i);
            builder.append(createPlayerSnapshotMessage(player));
            if (i < team.size() - 1) {
                builder.append(MessageUtil.color("&7, "));
                builder.getCurrent().setClickEvent(null);
                builder.getCurrent().setHoverEvent(null);
            }
        }

        return builder.create();
    }

    private BaseComponent[] createPlayerSnapshotMessage(PlayerSession player) {
        return new ChatComponentBuilder(ChatColor.YELLOW + player.getName())
                .attachToEachPart(
                        ChatHelper.hover(Messages.MATCH_RESULT_OVERVIEW_HOVER.match("{player}", player.getName())))
                .attachToEachPart(
                        ChatHelper.click("/viewsnapshot ".concat(snapshots.get(player).getId().toString())))
                .create();
    }

    public boolean isOnSameTeam(PlayerSession damagedPlayer, PlayerSession damagerPlayer) {
        Team team = getTeam(damagedPlayer);
        Preconditions.checkNotNull(team, "team");
        return team.hasPlayer(damagerPlayer);
    }

    public boolean isInMatch(PlayerSession player) {
        return getTeam(player) != null;
    }

    public boolean isAlive(PlayerSession player) {
        Team team = getTeam(player);
        Preconditions.checkNotNull(team, "team");
        return team.isAlive(player);
    }

    protected abstract @Nullable Team getTeam(PlayerSession player);

    protected abstract Team[] getLosers();

    protected abstract int getPlayersCount();

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
