package country.pvp.practice.match;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import country.pvp.practice.arena.Arena;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.SoloTeam;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.message.Bars;
import country.pvp.practice.message.MessagePattern;
import country.pvp.practice.message.Messages;
import country.pvp.practice.message.component.ChatComponentBuilder;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.visibility.VisibilityUpdater;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FreeForAllMatch extends Match {

    private final Set<SoloTeam> teams = Sets.newConcurrentHashSet();

    public FreeForAllMatch(MatchManager matchManager, VisibilityUpdater visibilityUpdater, LobbyService lobbyService, ItemBarService itemBarService, InventorySnapshotManager snapshotManager, Arena arena, Ladder ladder, boolean duel, SoloTeam... teams) {
        super(snapshotManager, matchManager, visibilityUpdater, lobbyService, itemBarService, arena, ladder, false, duel);
        this.teams.addAll(Arrays.stream(teams).collect(Collectors.toList()));
    }

    @Override
    void prepareTeams() {
        for (SoloTeam team : teams) {
            prepareTeam(team, arena.getCenter());
        }
        updateTeamVisibility();
    }

    @Override
    void onPreEnd() {
    }

    @Override
    void movePlayersToLobby() {
        for (SoloTeam team : teams) {
            lobbyService.moveToLobby(team.getPlayerSession());
        }
    }

    @Override
    void broadcastPlayerDeath(PlayerSession player) {
        if (player.hasLastAttacker()) {
            PlayerSession killer = player.getLastAttacker();

            for (SoloTeam team : teams) {
                Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                        new MessagePattern("{player}", getFormattedDisplayName(player, team)),
                        new MessagePattern("{killer}", getFormattedDisplayName(killer, team)));
            }
        } else {
            for (SoloTeam team : teams) {
                Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match(
                        new MessagePattern("{player}", getFormattedDisplayName(player, team)));
            }
        }
    }

    @Override
    void updateTeamVisibility() {
        for (SoloTeam team : teams) {
            for (SoloTeam otherTeam : teams) {
                PlayerSession player = team.getPlayerSession();
                PlayerSession other = otherTeam.getPlayerSession();
                visibilityUpdater.update(player, other);
                visibilityUpdater.update(other, player);
            }
        }
    }

    @Override
    void handleRespawn(PlayerSession player) {
        if (getAliveTeamsCount() <= 1) {
            Optional<SoloTeam> winnerOptional = getAliveTeam();
            end(winnerOptional.orElse(null));
            return;
        }

        setupSpectator(player);
    }

    @Override
    public void handleDisconnect(PlayerSession player) {
        createInventorySnapshot(player);

        for (SoloTeam team : teams) {
            broadcast(team, Messages.MATCH_PLAYER_DISCONNECTED.match("{player}", getFormattedDisplayName(player, team)));
        }

        player.handleDisconnectInMatch();

        if (state != MatchState.END && getAliveTeamsCount() <= 1) {
            Optional<SoloTeam> winnerOptional = getAliveTeam();
            end(winnerOptional.orElse(null));
        }
    }

    @Override
    void createInventorySnapshots() {
        for (SoloTeam team : teams) {
            PlayerSession player = team.getPlayerSession();
            if (team.isAlive(player)) createInventorySnapshot(player);
        }
    }

    @Override
    void sendResultComponent() {
        if (winner != null) {
            SoloTeam winnerTeam = (SoloTeam) winner;
            BaseComponent[] components = createFinalComponent(winnerTeam);

            for (PlayerSession player : getOnlinePlayers()) {
                player.sendComponent(components);
            }
        }
    }

    private BaseComponent[] createFinalComponent(SoloTeam winner) {
        BaseComponent[] winnerComponent = createComponent(winner, true);
        BaseComponent[] loserComponent = createComponent(winner, false);

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

    private BaseComponent[] createComponent(SoloTeam team, boolean winner) {
        ChatComponentBuilder builder = new ChatComponentBuilder(winner ? ChatColor.GREEN + "Winner: " : ChatColor.RED + "Loser: ");

        if (winner) {
            PlayerSession player = team.getPlayerSession();
            builder.append(createComponent(player));
        } else {
            for (SoloTeam loser : teams) {
                if (loser.equals(getWinner())) continue;
                builder.append(createComponent(loser.getPlayerSession()));
            }
        }

        return builder.create();
    }

    @Override
    public boolean areOnTheSameTeam(PlayerSession damagedPlayer, PlayerSession damagerPlayer) {
        return false;
    }

    @Override
    public boolean isInMatch(PlayerSession player) {
        return teams.stream().anyMatch(it -> it.getPlayerSession().equals(player));
    }

    @Override
    public boolean isAlive(PlayerSession player) {
        return getTeam(player).isAlive(player);
    }

    @Override
    int getPlayersCount() {
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

    public Team getTeam(PlayerSession player) {
        return teams.stream().filter(it -> it.getPlayerSession().equals(player)).findFirst().orElse(null);
    }

    private int getAliveTeamsCount() {
        return (int) teams.stream().filter(it -> !it.isDead()).count();
    }

    private Optional<SoloTeam> getAliveTeam() {
        return teams.stream().filter(it -> !it.isDead()).findFirst();
    }


    @Override
    List<PlayerSession> getOnlinePlayers() {
        List<PlayerSession> players = super.getOnlinePlayers();
        for (SoloTeam team : teams) {
            players.add(team.getPlayerSession());
        }
        return players;
    }
}
