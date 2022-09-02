package me.ponktacology.practice.util.visibility;

import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.type.TeamMatch;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.NameTags;
import lombok.experimental.UtilityClass;
import me.ponktacology.practice.player.data.PlayerState;
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
    public static Visibility provide(PracticePlayer observer, PracticePlayer observable) {
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

                    PlayerInfoTracker infoTracker = match.getInfoTracker();
                    if (!infoTracker.isAlive(observable)) {
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
                            ChatColor color = teamMatch.getRelativeColor(team);
                            NameTags.color(observer, observable, color, false);
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
