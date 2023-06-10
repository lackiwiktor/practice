package me.ponktacology.practice.util.visibility;

import lombok.experimental.UtilityClass;
import me.ponktacology.practice.Practice;
import me.ponktacology.practice.follow.FollowService;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.match.type.TeamMatch;
import me.ponktacology.practice.party.Party;
import me.ponktacology.practice.party.PartyService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.NameTags;
import org.bukkit.ChatColor;

@UtilityClass
public class VisibilityProvider {

  /**
   * Decides the visibility of a player
   *
   * @param observer player who is looking
   * @param observable player who is looked on
   * @return visibility
   */
  public static Visibility provide(PracticePlayer observer, PracticePlayer observable) {
    if (observer.equals(observable)) {
      //  NameTags.reset(observer.getPlayer(), observable.getPlayer());
      return Visibility.SHOWN;
    }

    MatchService matchService = Practice.getService(MatchService.class);

    if (matchService.isInMatch(observer)) {
      Match match = matchService.getPlayerMatch(observer);
      if (match.isParticipating(observable)) {

        Team team = match.getTeam(observable);
        boolean showHP = match.getLadder().isShowHP();
        if (team.hasPlayer(observer)) {
          NameTags.color(observer, observable, ChatColor.GREEN, showHP);
        } else {
          NameTags.color(observer, observable, ChatColor.RED, showHP);
        }

        PlayerInfoTracker infoTracker = match.getInfoTracker();
        if (!infoTracker.isAlive(observable)) {
          return Visibility.HIDDEN;
        }

        return Visibility.SHOWN;
      }

      return Visibility.HIDDEN;
    }

    switch (observer.getState()) {
      case QUEUING:
      case IN_LOBBY:
        FollowService followService = Practice.getService(FollowService.class);

        if (followService.isFollowing(observer)) {
          if (followService.getFollowingPlayer(observer).equals(observable)) {
            NameTags.color(observer, observable, ChatColor.BLUE, false);
            return Visibility.SHOWN;
          }
        }

        PartyService partyService = Practice.getService(PartyService.class);
        if (partyService.hasParty(observer) && partyService.hasParty(observable)) {
          Party party = partyService.getPlayerParty(observable);

          if (party.hasPlayer(observable)) {
            NameTags.color(observer, observable, ChatColor.BLUE, false);
            return Visibility.SHOWN;
          }
        }

        NameTags.reset(observer.getPlayer(), observable.getPlayer());
        return Visibility.HIDDEN;
      case SPECTATING:
        if (matchService.isInMatch(observable)) {
          Match match = observer.getCurrentlySpectatingMatch();

          if (match.isParticipating(observable)) {
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
