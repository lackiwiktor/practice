package country.pvp.practice.match;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import country.pvp.practice.Messages;
import country.pvp.practice.arena.DuplicatedArena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshot;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.player.data.PlayerState;
import country.pvp.practice.util.PlayerUtil;
import country.pvp.practice.util.TaskDispatcher;
import country.pvp.practice.util.message.Bars;
import country.pvp.practice.util.message.MessageUtil;
import country.pvp.practice.util.message.Recipient;
import country.pvp.practice.util.message.Sender;
import country.pvp.practice.util.message.component.ChatComponentBuilder;
import country.pvp.practice.util.message.component.ChatHelper;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Data
public abstract class Match implements Recipient {

    protected final VisibilityUpdater visibilityUpdater;
    protected final LobbyService lobbyService;
    protected final ItemBarService itemBarService;
    protected final DuplicatedArena arena;
    protected final Ladder ladder;
    protected final boolean ranked;
    protected final boolean duel;
    protected final Set<PlayerSession> spectators = Sets.newHashSet();
    protected final Map<PlayerSession, InventorySnapshot> snapshots = Maps.newHashMap();

    protected MatchState state = MatchState.STARTING_ROUND;
    protected @Nullable Team winner;
    protected int roundsLeft;

    private final UUID id = UUID.randomUUID();
    private final MatchLogicTask logicTask = new MatchLogicTask(this);
    private final MatchManager matchManager;
    private final InventorySnapshotManager snapshotManager;


    private BukkitRunnable countDownRunnable;
    protected long startedOn = System.currentTimeMillis();

    protected Match(InventorySnapshotManager snapshotManager,
                    MatchManager matchManager,
                    VisibilityUpdater visibilityUpdater,
                    LobbyService lobbyService,
                    ItemBarService itemBarService,
                    DuplicatedArena arena,
                    Ladder ladder,
                    boolean ranked,
                    boolean duel) {
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
        arena.setOccupied(true);
        TaskDispatcher.scheduleSync(logicTask, 1L, TimeUnit.SECONDS);
        TaskDispatcher.sync(this::start);
    }

    private void start() {
        prepareTeams();
        resetTeams();
        onRoundStart();
    }

    public void onRoundStart() {
        snapshots.clear();
        prepareTeams();
        startedOn = System.currentTimeMillis();
    }

    protected void prepareTeam(Team team) {
        team.createMatchSession(this);
        team.clearRematchData();
    }

    protected void resetTeam(Team team, Location spawnLocation) {
        Preconditions.checkNotNull(spawnLocation);
        Preconditions.checkNotNull(spawnLocation.getBlock());
        team.reset();
        team.giveKits(ladder);
        team.teleport(spawnLocation.getBlock().getType() == Material.AIR ?
                spawnLocation : spawnLocation.add(0, 2, 0));
    }

    protected abstract void prepareTeams();

    protected abstract void resetTeams();

    public abstract boolean canStartRound();

    public void onRoundEnd() {
        state = MatchState.ENDING_ROUND;
        createInventorySnapshots();
        snapshots.values().forEach(it -> it.setCreatedAt(System.currentTimeMillis()));
        snapshotManager.addAll(snapshots.values());
        sendResultComponent();

    }

    protected void end() {
        state = MatchState.ENDING_MATCH;
        handleEnd();
        movePlayersToLobby();
        moveSpectatorsToLobby();
        arena.cleanUp();
        finish();
    }

    protected abstract void handleEnd();

    private void finish() {
        matchManager.remove(this);
        arena.setOccupied(false);
    }

    public void cancel(String reason) {
        broadcast(Messages.MATCH_CANCELLED.match("{reason}", reason));
        end();
    }

    protected void movePlayersToLobby() {
        for (PlayerSession session : getOnlinePlayers()) {
            lobbyService.moveToLobby(session);
        }
    }

    private void moveSpectatorsToLobby() {
        for (PlayerSession spectator : spectators) {
            stopSpectating(spectator, false);
        }
    }

    public void handlePlayerDeath(PlayerSession deadPlayer, List<ItemStack> drops) {
        createInventorySnapshot(deadPlayer);
        deadPlayer.setDead(true);
        broadcastPlayerDeath(deadPlayer);
        updateVisibility();
        PlayerUtil.resetPlayer(deadPlayer.getPlayer());
        sendDeathPackets(deadPlayer);
        deadPlayer.setVelocity(new Vector());

        //Remove Kit Books
        drops.removeIf(it -> deadPlayer.getMatchingKit(ladder, it) != null);

        if (canEndRound()) {
            //We don't need items of last dead player
            drops.clear();

            state = MatchState.ENDING_ROUND;
            startedOn = System.currentTimeMillis() - startedOn;
            onRoundEnd();

            if (canEndMatch()) {
                state = MatchState.ENDING_MATCH;
            } else {
                TaskDispatcher.runLater(() -> arena.cleanUp(), 100L, TimeUnit.MILLISECONDS);
            }

            logicTask.setNextAction(4);
        } else {
            deadPlayer.enableFlying();
        }
    }

    protected abstract boolean canEndMatch();

    public void handlePlayerDisconnect(PlayerSession disconnectedPlayer) {
        createInventorySnapshot(disconnectedPlayer);
        broadcastPlayerDisconnect(disconnectedPlayer);
        disconnectedPlayer.handleDisconnectInMatch();

        if (canEndRound()) {
            state = MatchState.ENDING_ROUND;
            startedOn = System.currentTimeMillis() - startedOn;
            onRoundEnd();

            if (canEndMatch())
                state = MatchState.ENDING_MATCH;
        }
    }

    protected abstract void broadcastPlayerDisconnect(PlayerSession disconnectedPlayer);

    protected void broadcastPlayerDisconnect(Team team, PlayerSession disconnectedPlayer) {
        broadcast(team, Messages.MATCH_PLAYER_DISCONNECTED.match("{player}", getFormattedDisplayName(disconnectedPlayer, team)));
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

    public abstract boolean canEndRound();

    protected void updateVisibility() {
        for (PlayerSession session : getAllOnlinePlayers()) {
            visibilityUpdater.update(session);
        }
    }

    protected void updateVisibility(boolean flicker) {
        for (PlayerSession session : getAllOnlinePlayers()) {
            visibilityUpdater.update(session, flicker);
        }
    }

    protected void broadcast(String message) {
        Sender.message(this, message);
    }

    protected void broadcast(Messages message) {
        Sender.message(this, message);
    }

    protected void broadcast(country.pvp.practice.match.team.Team team, String message) {
        Sender.message(team, message);
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
        updateVisibility();
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
            Sender.message(player, Bars.CHAT_BAR);
            player.sendComponent(components);
            Sender.message(player, Bars.CHAT_BAR);
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

    public void addPlacedBlock(Block block) {
        arena.addPlacedBlock(block);
    }

    public boolean hasBeenPlacedByPlayer(Block block) {
        return arena.hasBeenPlacedByPlayer(block);
    }

    public void removePlacedBlock(Block block) {
        arena.removePlacedBlock(block);
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

    public boolean isInArena(Location location) {
        return arena.isIn(location);
    }
}
