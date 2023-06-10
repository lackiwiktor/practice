package me.ponktacology.practice.match.team.type;

import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;

import java.util.Collection;

public class MultiTeam extends Team {

    private MultiTeam(Collection<PracticePlayer> players) {
        super();
        this.players.addAll(players);
    }

    public static MultiTeam of(Collection<PracticePlayer> players) {
        return new MultiTeam(players);
    }

    @Override
    public String getName() {
        if (players.size() == 0) return "invalid";
        return players.get(0).getName();
    }
}
