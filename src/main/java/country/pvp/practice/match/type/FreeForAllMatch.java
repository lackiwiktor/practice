package country.pvp.practice.match.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import country.pvp.practice.Messages;
import country.pvp.practice.arena.DuplicatedArena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchManager;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.message.MessagePattern;
import country.pvp.practice.visibility.VisibilityUpdater;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FreeForAllMatch extends Match {

    private final Set<Team> teams = Sets.newConcurrentHashSet();

    public FreeForAllMatch(MatchManager matchManager,
                           VisibilityUpdater visibilityUpdater,
                           LobbyService lobbyService,
                           ItemBarService itemBarService,
                           InventorySnapshotManager snapshotManager,
                           DuplicatedArena arena,
                           Ladder ladder,
                           boolean duel,
                           SoloTeam... teams) {
        super(snapshotManager, matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, false, duel);
        this.teams.addAll(Arrays.stream(teams).collect(Collectors.toList()));
        Preconditions.checkState(teams.length >= 2, "Invalid team size for FFA match");
    }

    @Override
    protected void prepareTeams() {
        for (Team team : teams) {
            prepareTeam(team);
        }
    }

    @Override
    protected void resetTeams() {
        for (Team team : teams) {
            resetTeam(team, arena.getCenter());
        }

        updateVisibility(true);
    }

    @Override
    public boolean canStartRound() {
        return false;
    }

    @Override
    protected void handleEnd() {
    }

    @Override
    protected boolean canEndMatch() {
        return getAliveTeamsCount() <= 1;
    }

    @Override
    protected void broadcastPlayerDisconnect(PlayerSession disconnectedPlayer) {
        for (Team team : teams) {
            broadcastPlayerDisconnect(team, disconnectedPlayer);
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
    public boolean canEndRound() {
        if (getAliveTeamsCount() <= 1) {
            getAliveTeam().ifPresent(it -> winner = it);
            return true;
        }

        return false;
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
        List<String> lines = Lists.newArrayList();

        lines.add(ChatColor.YELLOW + "Teams: " + ChatColor.WHITE + getAliveTeamsCount() + "/" + teams.size());
        lines.add(" ");

        return lines;
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
