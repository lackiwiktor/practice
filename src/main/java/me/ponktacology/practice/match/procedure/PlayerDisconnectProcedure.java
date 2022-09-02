package me.ponktacology.practice.match.procedure;

import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.message.Messenger;

import java.util.List;

public class PlayerDisconnectProcedure {

  private final Match match;
  private final PracticePlayer disconnectedPlayer;
  private final PlayerInfoTracker infoTracker;
  private final List<Team> teams;

  public PlayerDisconnectProcedure(Match match, PracticePlayer disconnectedPlayer) {
    this.match = match;
    this.disconnectedPlayer = disconnectedPlayer;
    this.infoTracker = match.getInfoTracker();
    this.teams = match.getTeams();
    init();
  }

  private void init() {
    broadcastPlayerDisconnect();

    infoTracker.setDisconnected(disconnectedPlayer, true);

    if (infoTracker.isAlive(disconnectedPlayer)) {
      disconnectedPlayer.die();
    }
  }

  protected void broadcastPlayerDisconnect() {
    teams.forEach(
        team ->
            Messenger.message(
                team,
                Messages.MATCH_PLAYER_DISCONNECTED.match(
                    "{player}", match.getFormattedDisplayName(disconnectedPlayer, team))));
  }
}
