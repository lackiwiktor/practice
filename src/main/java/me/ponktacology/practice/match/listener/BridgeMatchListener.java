package me.ponktacology.practice.match.listener;

import lombok.RequiredArgsConstructor;
import me.ponktacology.practice.match.Match;
import me.ponktacology.practice.match.MatchService;
import me.ponktacology.practice.match.MatchState;
import me.ponktacology.practice.match.event.MatchPlayerDamageByPlayerEvent;
import me.ponktacology.practice.match.event.MatchStartCountdownEvent;
import me.ponktacology.practice.match.event.MatchStartEvent;
import me.ponktacology.practice.match.team.Team;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.PracticePlayerListener;
import me.ponktacology.practice.util.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class BridgeMatchListener extends PracticePlayerListener {

  private final MatchService matchService;

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onBedBreak(BlockBreakEvent event) {
    if (!event.getBlock().getType().toString().contains("BED")) return;
    Location bedLocation = event.getBlock().getLocation();
    PracticePlayer player = get(event.getPlayer());
    if (!matchService.isInMatch(player)) return;
    Match match = matchService.getPlayerMatch(player);
    if (!match.getLadder().isBridge() || match.getState() != MatchState.IN_PROGRESS) return;
    event.setCancelled(true);
    Team team = match.getTeam(player);
    Location spawnLoc = match.getSpawnLocation(team);
    int distance = (int) spawnLoc.distanceSquared(bedLocation);
    if (distance > 100) {
      if (team.getStatistics().increaseBridgeScore() >= 3) {
        match.endMatch(team);
      } else {
        match.forEachTeam(
            matchTeam -> {
              Location spawnLocation = match.getSpawnLocation(matchTeam);
              match
                  .getOnlinePlayers(matchTeam)
                  .forEach(teamPlayer -> teamPlayer.teleport(spawnLocation));
            });
      }
    }
  }

  @EventHandler
  public void onBedEnter(PlayerInteractEvent event) {
    if (!event.getAction().toString().contains("RIGHT")) return;
    PracticePlayer player = get(event.getPlayer());
    if (!matchService.isInMatch(player)) return;
    Match match = matchService.getPlayerMatch(player);
    if (!match.getLadder().isBridge()) return;
    Block clickedBlock = event.getClickedBlock();
    if (clickedBlock != null && clickedBlock.getType().toString().contains("BED")) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerDamageByPlayer(MatchPlayerDamageByPlayerEvent event) {
    if (event.getMatch().getLadder().isBridge()) {
      event.getBukkitEvent().setDamage(0);
    }
  }

  @EventHandler
  public void onPlace(BlockPlaceEvent event) {
    PracticePlayer player = get(event.getPlayer());
    if (!matchService.isInMatch(player)) return;
    if (matchService.getPlayerMatch(player).getLadder().isBridge()) {
      event.getItemInHand().setAmount(64);
      event.getPlayer().updateInventory();
    }
  }

  @EventHandler
  public void onMatchStartCountdown(MatchStartCountdownEvent event) {
    Match match = event.getMatch();
    if (!match.getLadder().isBridge()) {
      return;
    }

    for (Team team : match.getTeams()) {
      for (PracticePlayer player : match.getOnlinePlayers(team)) {
        PlayerUtil.denyMovement(player.getPlayer());
      }
    }
  }

  @EventHandler
  public void onMatchStart(MatchStartEvent event) {
    Match match = event.getMatch();
    if (!match.getLadder().isBridge()) {
      return;
    }

    for (Team team : match.getTeams()) {
      for (PracticePlayer player : match.getOnlinePlayers(team)) {
        PlayerUtil.allowMovement(player.getPlayer());
      }
    }
  }
}
