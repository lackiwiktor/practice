package country.pvp.practice.visibility;

import country.pvp.practice.match.Match;
import country.pvp.practice.match.MatchData;
import country.pvp.practice.player.PlayerState;
import country.pvp.practice.player.PracticePlayer;

public class VisibilityProvider {

    /**
     * Decides the visibility of a player
     *
     * @param observer   player who is looking
     * @param observable player who is looked on
     * @return visibility
     */
    public Visibility provide(PracticePlayer observer, PracticePlayer observable) {
        if (observer.isInLobby()) {
            return Visibility.HIDDEN;
        }

        if (observer.isInMatch()) {
            if (observable.isInMatch()) {
                MatchData matchData = observer.getStateData(PlayerState.IN_MATCH);
                Match match = matchData.getMatch();

                if (match.isInMatch(observable)) return Visibility.SHOWN;
                else return Visibility.HIDDEN;
            } else return Visibility.HIDDEN;
        }


        return Visibility.SHOWN;
    }
}
