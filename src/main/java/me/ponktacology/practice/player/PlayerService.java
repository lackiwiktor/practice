package me.ponktacology.practice.player;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.player.listener.PreparePlayerListener;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerService extends Service {

  private final PlayerRepository playerRepository = new PlayerRepository();
  private final Map<UUID, PracticePlayer> players = Maps.newConcurrentMap();

  @Override
  public void configure() {
    addListener(new PreparePlayerListener(this));

    for (Player player : Bukkit.getOnlinePlayers()) {
      PracticePlayer practicePlayer = new PracticePlayer(player.getUniqueId(), player.getName());

      try {
        create(practicePlayer);
      } catch (Exception e) {
        player.kickPlayer("Timed Out");
      }
    }
  }

  @Override
  public void stop() {
    players.values().forEach(playerRepository::save);
    players.clear();
  }

  public List<PracticePlayer> get(Bson filter, Bson sort, int count) {
    return playerRepository.get(filter, sort, count);
  }

  public void createIndex(Bson index) {
    playerRepository.createIndex(index);
  }


  public PracticePlayer get(Player player) {
    return Optional.ofNullable(players.get(player.getUniqueId()))
        .orElseThrow(() -> new PlayerNotOnlineException(player));
  }

  public Optional<PracticePlayer> get(String name) {
    return players.values().stream().filter(it -> it.getName().equalsIgnoreCase(name)).findFirst();
  }

  public void add(PracticePlayer player) {
    players.put(player.getUuid(), player);
  }

  public @Nullable PracticePlayer remove(Player player) {
    return players.remove(player.getUniqueId());
  }

  public Set<PracticePlayer> getAll() {
    return new HashSet<>(players.values());
  }

  public void create(PracticePlayer player) {
    playerRepository.load(player);
    add(player);
  }

  public void delete(Player player) {
    PracticePlayer practicePlayer = remove(player);

    if (practicePlayer != null) {
      playerRepository.save(practicePlayer);
    }
  }
}
