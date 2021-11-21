package country.pvp.practice.visibility;

import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchData;
import country.pvp.practice.player.PracticePlayer;
import country.pvp.practice.player.data.PlayerState;

public class VisibilityProvider {

    /**
     * Decides the visibility of a player
     *
     * @param observer   player who is looking
     * @param observable player who is looked on
     * @return visibility
     */
    public Visibility provide(PracticePlayer observer, PracticePlayer observable) {
        if (observer.isInLobby() || observer.isInQueue() || observer.isInEditor()) {
            return Visibility.HIDDEN;
        }

        if (observer.isInMatch()) {
            if (observable.isInMatch()) {
                MatchData matchData = observer.getStateData(PlayerState.IN_MATCH);
                Match match = matchData.getMatch();

                if (match.isInMatch(observable)) {
                    if (!match.isAlive(observable)) {
                        return Visibility.HIDDEN;
                    }

                    return Visibility.SHOWN;
                } else {
                    return Visibility.HIDDEN;
                }
            } else return Visibility.HIDDEN;
        }


        return Visibility.SHOWN;
    }
}
