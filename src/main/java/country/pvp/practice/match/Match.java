package country.pvp.practice.match;

import country.pvp.practice.arena.Arena;
import country.pvp.practice.ladder.Ladder;
import country.pvp.practice.team.Team;
import lombok.Data;
import org.bukkit.Location;

@Data
public class Match {

    private final Ladder ladder;
    private final Arena arena;
    private final Team teamA;
    private final Team teamB;

    private void prepareTeams() {

    }

    private void prepareTeam(Team team, Location spawnLocation) {
        team.teleport(spawnLocation);
    }
}
