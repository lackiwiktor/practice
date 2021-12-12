package country.pvp.practice.match.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.MatchState;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messages;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FreeForAllMatch extends Match {

    private final Set<Team> teams = Sets.newConcurrentHashSet();

    public FreeForAllMatch(MatchManager matchManager, VisibilityUpdater visibilityUpdater, LobbyService lobbyService, ItemBarService itemBarService, InventorySnapshotManager snapshotManager, Arena arena, Ladder ladder, boolean duel, SoloTeam... teams) {
        super(snapshotManager, matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, false, duel);
        this.teams.addAll(Arrays.stream(teams).collect(Collectors.toList()));
    }

    @Override
    protected void prepareTeams() {
        for (Team team : teams) {
            prepareTeam(team, arena.getCenter());
        }

        updateVisibility();
    }

    @Override
    protected void handleEnd() {
    }

    @Override
    protected void movePlayersToLobby() {
        for (Team team : teams) {
            moveTeamToLobby(team);
        }
    }

    @Override
    protected void broadcastPlayerDeath(PlayerSession player) {
        if (player.hasLastAttacker()) {
            PlayerSession killer = player.getLastAttacker();

            for (Team team : teams) {
                broadcast(team, Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                        new MessagePattern("{player}", getFormattedDisplayName(player, team)),
                        new MessagePattern("{killer}", getFormattedDisplayName(killer, team))));
            }
        } else {
            for (Team team : teams) {
                broadcast(team, Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match(
                        new MessagePattern("{player}", getFormattedDisplayName(player, team))));
            }
        }
    }

    @Override
    protected void tryEndingMatch(PlayerSession player) {
        if (getAliveTeamsCount() <= 1) {
            Optional<Team> winnerOptional = getAliveTeam();
            end(winnerOptional.orElse(null));
        }
    }

    @Override
    public void handleDisconnect(PlayerSession player) {
        createInventorySnapshot(player);

        for (Team team : teams) {
            broadcast(team, Messages.MATCH_PLAYER_DISCONNECTED.match("{player}", getFormattedDisplayName(player, team)));
        }

        player.handleDisconnectInMatch();

        if (state != MatchState.END && getAliveTeamsCount() <= 1) {
            Optional<Team> winnerOptional = getAliveTeam();
            end(winnerOptional.orElse(null));
        }
    }

    @Override
    protected void createInventorySnapshots() {
        for (Team team : teams) {
            for (PlayerSession player : team.getOnlinePlayers()) {
                if (team.isAlive(player)) createInventorySnapshot(player);
            }
        }
    }

    @Override
    protected Team[] getLosers() {
        Preconditions.checkNotNull(winner, "winner");
        return teams.stream().filter(it -> !it.equals(winner)).toArray(Team[]::new);
    }

    @Override
    protected int getPlayersCount() {
        return teams.size();
    }

    @Override
    public List<String> getBoard(PlayerSession player) {
        return Lists.newArrayList();
    }

    @Override
    public void receive(String message) {
        for (Team team : teams) {
            team.receive(message);
        }
    }

    @Override
    public Team getTeam(PlayerSession player) {
        return teams.stream().filter(it -> it.hasPlayer(player)).findFirst().orElse(null);
    }

    private int getAliveTeamsCount() {
        return (int) teams.stream().filter(it -> !it.isDead()).count();
    }

    private Optional<Team> getAliveTeam() {
        return teams.stream().filter(it -> !it.isDead()).findFirst();
    }

    @Override
    protected List<PlayerSession> getOnlinePlayers() {
        List<PlayerSession> players = Lists.newArrayList();
        for (Team team : teams) {
            players.addAll(team.getOnlinePlayers());
        }
        return players;
    }
}
