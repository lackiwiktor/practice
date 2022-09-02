package me.ponktacology.practice.lobby;

import me.ponktacology.practice.Practice;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.hotbar.HotBarService;
import me.ponktacology.practice.lobby.listener.LobbyListener;
import me.ponktacology.practice.player.PlayerService;
import me.ponktacology.practice.player.PracticePlayer;
import me.ponktacology.practice.player.data.PlayerState;
import me.ponktacology.practice.util.PlayerUtil;
import me.ponktacology.practice.util.visibility.VisibilityService;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LobbyService extends Service {

  @Override
  public void configure() {
    addListener(new LobbyListener(this));

    for (PracticePlayer player : Practice.getService(PlayerService.class).getAll()) {
      moveToLobby(player);
    }
  }

  public boolean shouldRebound(Location location) {
    return location.getY() < 50;
  } // TODO: From configuration

  public void moveToLobby(PracticePlayer player) {
    prepareForLobby(player);
    player.teleport(getSpawnLocation());
  }

  public Location getSpawnLocation() {
    Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
    spawn.add(0.5, 0, 0.5); // 'prettify' so players spawn in middle of block
    return spawn;
  }

  public void prepareForLobby(PracticePlayer player) {
    player.setState(PlayerState.IN_LOBBY);
    PlayerUtil.resetPlayer(player.getPlayer());
    player.disableFlying();
    Practice.getService(HotBarService.class).apply(player);
    Practice.getService(VisibilityService.class).update(player);
  }
}
