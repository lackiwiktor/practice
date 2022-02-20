package country.pvp.practice.match;

import com.google.inject.Inject;
import com.mongodb.lang.Nullable;
import country.pvp.practice.arena.DuplicatedArena;
import country.pvp.practice.arena.DuplicatedArenaManager;
import country.pvp.practice.itembar.ItemBarService;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.lobby.LobbyService;
import country.pvp.practice.match.snapshot.InventorySnapshotManager;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.match.team.type.SoloTeam;
import country.pvp.practice.match.type.FreeForAllMatch;
import country.pvp.practice.match.type.TeamMatch;
import country.pvp.practice.player.PlayerRepository;
import country.pvp.practice.util.message.Sender;
import country.pvp.practice.visibility.VisibilityUpdater;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class MatchProvider {

    private final VisibilityUpdater visibilityUpdater;
    private final LobbyService lobbyService;
    private final MatchManager matchManager;
    private final ItemBarService itemBarService;
    private final DuplicatedArenaManager arenaManager;
    private final InventorySnapshotManager snapshotManager;
    private final PlayerRepository playerRepository;

    public @Nullable TeamMatch provide(Ladder ladder, boolean ranked, boolean duel, Team teamA, Team teamB) {
        DuplicatedArena arena = arenaManager.getRandom();

        if (arena == null) {
            Sender.messageError(teamA, "No arenas are available right now.");
            Sender.messageError(teamB, "No arenas are available right now.");

            teamA.getOnlinePlayers().forEach(it -> itemBarService.apply(it));
            teamB.getOnlinePlayers().forEach(it -> itemBarService.apply(it));
            return null;
        }


        return new TeamMatch(matchManager,
                visibilityUpdater,
                lobbyService,
                itemBarService,
                arena,
                ladder,
                ranked,
                duel,
                snapshotManager,
                playerRepository,
                teamA,
                teamB);
    }

    public @Nullable FreeForAllMatch provide(Ladder ladder, SoloTeam... teams) {
        DuplicatedArena arena = arenaManager.getRandom();

        if (arena == null) {
            for (Team team : teams) {
                Sender.messageError(team, "No arenas are available right now.");

                team.getOnlinePlayers().forEach(it -> itemBarService.apply(it));
                return null;
            }
        }

        return new FreeForAllMatch(matchManager,
                visibilityUpdater,
                lobbyService,
                itemBarService,
                snapshotManager,
                arena,
                ladder,
                false,
                teams);
    }

}
