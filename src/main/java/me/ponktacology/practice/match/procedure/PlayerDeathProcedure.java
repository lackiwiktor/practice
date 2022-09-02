package me.ponktacology.practice.match.procedure;

import me.ponktacology.practice.Messages;
import me.ponktacology.practice.match.DeathPackets;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.info.PlayerInfoTracker;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.util.PlayerUtil;
import me.ponktacology.practice.util.message.MessagePattern;
import me.ponktacology.practice.util.message.Messenger;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class PlayerDeathProcedure {

  private final Match match;
  private final PracticePlayer player;
  private final PlayerInfoTracker infoTracker;
  private final List<Team> teams;
  private final List<ItemStack> drops;
  public PlayerDeathProcedure(
      Match match, PracticePlayer player, List<ItemStack> drops) {
    this.match = match;
    this.player = player;
    this.drops = drops;
    this.infoTracker = match.getInfoTracker();
    this.teams = match.getTeams();
    init();
  }

  private void init() {
    match.createInventorySnapshot(player);

    infoTracker.setDead(player, true);

    if (!infoTracker.isDisconnected(player)) {
      match.updateVisibility(false);
      player.setVelocity(new Vector());
      PlayerUtil.resetPlayer(player.getPlayer());
    }

    broadcastPlayerDeath();
    new DeathPackets(match, player).sendDeathPackets();

    // Remove Kit Books
    drops.removeIf(it -> player.getMatchingKit(match.getLadder(), it) != null);

    if (match.canEndRound()) {
      drops.clear();
      match.setState(MatchState.ENDING);
    } else {
      match.startSpectating(player, player);
    }
  }

  private void broadcastPlayerDeath() {
    PlayerInfoTracker statisticsTracker = match.getInfoTracker();
    boolean hasLastAttacker = infoTracker.hasLastAttacker(player);

    teams.forEach(
        team -> {
          if (hasLastAttacker) {
            PracticePlayer killer = statisticsTracker.getLastAttacker(player);

            Messenger.message(
                team,
                Messages.MATCH_PLAYER_KILLED_BY_PLAYER.match(
                    new MessagePattern("{player}", match.getFormattedDisplayName(player, team)),
                    new MessagePattern("{killer}", match.getFormattedDisplayName(killer, team))));
          } else {
            Messenger.message(
                team,
                Messages.MATCH_PLAYER_KILLED_BY_UNKNOWN.match(
                    new MessagePattern("{player}", match.getFormattedDisplayName(player, team))));
          }
        });
  }
}
