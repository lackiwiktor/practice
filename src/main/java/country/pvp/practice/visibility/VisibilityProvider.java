package country.pvp.practice.visibility;

import country.pvp.practice.match.Match;
import country.pvp.practice.match.team.Team;
import country.pvp.practice.match.type.TeamMatch;
import country.pvp.practice.party.Party;
import country.pvp.practice.player.PlayerSession;
import country.pvp.practice.util.NameTags;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class VisibilityProvider {

    /**
     * Decides the visibility of a player
     *
     * @param observer   player who is looking
     * @param observable player who is looked on
     * @return visibility
     */
    public static Visibility provide(PlayerSession observer, PlayerSession observable) {
        if (observer.equals(observable)) {
            NameTags.color(observer, observable, ChatColor.GREEN, false);
            return Visibility.SHOWN;
        }

        switch (observer.getState()) {
            case QUEUING:
            case IN_LOBBY:
                if (observer.hasParty() && observable.hasParty()) {
                    Party party = observer.getParty();

                    if (party.hasPlayer(observable)) {
                        NameTags.color(observer, observable, ChatColor.BLUE, false);
                        return Visibility.SHOWN;
                    }
                }
                return Visibility.HIDDEN;
            case IN_MATCH:
                if (observable.isInMatch()) {
                    Match match = observable.getCurrentMatch();
                    Team team = match.getTeam(observable);

                    if (team.hasPlayer(observer)) {
                        NameTags.color(observer, observable, ChatColor.GREEN, false);
                    } else {
                        NameTags.color(observer, observable, ChatColor.RED, false);
                    }

                    if (!match.isAlive(observable)) {
                        return Visibility.HIDDEN;
                    }

                    return Visibility.SHOWN;
                }

                return Visibility.HIDDEN;
            case SPECTATING:
                if (observable.isInMatch()) {
                    Match match = observer.getCurrentlySpectatingMatch();

                    if (match.isInMatch(observable)) {
                        if (match instanceof TeamMatch) {
                            TeamMatch teamMatch = (TeamMatch) match;

                            Team team = match.getTeam(observable);
                            if (teamMatch.getTeamA().equals(team)) {
                                NameTags.color(observer, observable, ChatColor.GREEN, false);
                            } else {
                                NameTags.color(observer, observable, ChatColor.BLUE, false);
                            }
                        }
                        return Visibility.SHOWN;
                    }

                    return Visibility.HIDDEN;
                }
                return Visibility.HIDDEN;
            default:
                return Visibility.HIDDEN;
        }
    }
}
