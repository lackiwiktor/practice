package country.pvp.practice.visibility;

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
        if (observable.isInLobby()) {
            return Visibility.HIDDEN;
        }

        return Visibility.SHOWN;
    }
}
