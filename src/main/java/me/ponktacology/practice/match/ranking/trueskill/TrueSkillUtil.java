package me.ponktacology.practice.match.ranking.trueskill;


import de.gesundkrank.jskills.*;
import de.gesundkrank.jskills.trueskill.TwoPlayerTrueSkillCalculator;
import me.vaperion.blade.util.Tuple;

import java.util.Collection;
import java.util.Map;

public class TrueSkillUtil {

    public static final int INITIAL_MEAN = 25;
    public static final double STD = 8.333333333333334;

    private static final TwoPlayerTrueSkillCalculator calculator = new TwoPlayerTrueSkillCalculator();
    private static final GameInfo gameInfo = new GameInfo(
            INITIAL_MEAN,
            STD,
            4.166666666666667,
            0.08333333333333333,
            0.1);


    public static Tuple<Rating, Rating> getNewRatings(Rating winner, Rating loser) {
        Player<Integer> player1 = new Player<>(1);
        Player<Integer> player2 = new Player<>(2);
        Team team1 = new Team(player1, winner);
        Team team2 = new Team(player2, loser);
        Collection<ITeam> teams = Team.concat(team1, team2);
        Map<IPlayer, Rating> newRatings = calculator.calculateNewRatings(gameInfo, teams, 1, 2);
        Rating player1NewRating = newRatings.get(player1);
        Rating player2NewRating = newRatings.get(player2);
        return new Tuple(player1NewRating, player2NewRating);
    }
}
