package country.pvp.practice.visibility;

import country.pvp.practice.match.Match;
import country.pvp.practice.match.PlayerMatchData;
import country.pvp.practice.match.PlayerSpectatingData;
import country.pvp.practice.player.PracticePlayer;
import org.jetbrains.annotations.NotNull;

public class VisibilityProvider {

    /**
     * Decides the visibility of a player
     *
     * @param observer   player who is looking
     * @param observable player who is looked on
     * @return visibility
     */
    public Visibility provide( PracticePlayer observer, PracticePlayer observable) {
        switch (observer.getState()) {
            case IN_MATCH:
                if (observable.isInMatch()) {
                    PlayerMatchData matchData = observable.getStateData();
                    Match match = matchData.getMatch();
                    return match.isInMatch(observer) && match.isAlive(observable) ? Visibility.SHOWN : Visibility.HIDDEN;
                }
                return Visibility.HIDDEN;
            case SPECTATING:
                if (observable.isInMatch()) {
                    PlayerSpectatingData spectatingData = observer.getStateData();

                    return spectatingData.getMatch().isInMatch(observable) ? Visibility.SHOWN : Visibility.HIDDEN;
                }
                return Visibility.HIDDEN;
            default:
                return Visibility.HIDDEN;
        }
    }
}
