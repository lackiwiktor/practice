package me.ponktacology.practice.match.type;

import com.google.common.collect.Sets;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import me.ponktacology.practice.ladder.Ladder;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import org.bukkit.Location;

import java.util.*;

public class FreeForAllMatch extends Match {

    private final Set<Team> teams = Sets.newHashSet();

    public FreeForAllMatch(Ladder ladder,
                           boolean ranked,
                           boolean duel,
                           MatchArenaCopy arena,
                           Team... teams) {
        super(ladder, ranked, duel, arena);
        this.teams.addAll(Arrays.asList(teams));
    }


    @Override
    public boolean canEndRound() {
        return teams.stream().filter(it -> !isTeamDead(it)).count() <= 1;
    }

    @Override
    public List<String> getBoard(PracticePlayer player) {
        return Collections.singletonList(
                "Teams: " + getDeadTeamCount()
                        + "/" +
                        getTeams().size());
    }

    private long getDeadTeamCount() {
        return getTeams()
                .stream()
                .filter(it -> !isTeamDead(it))
                .count();
    }

    @Override
    protected Location getSpawnLocation(Team team) {
        return getArena().getCenter();
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }
}
