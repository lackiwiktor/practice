package country.pvp.practice.match.team;

import country.pvp.practice.ladder.Ladder;

public interface Ranked {

    int getElo(Ladder ladder);

    void setElo(Ladder ladder, int elo);
}
