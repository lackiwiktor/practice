package me.ponktacology.practice.arena;

import com.google.common.collect.Maps;
import me.ponktacology.practice.Service;
import me.ponktacology.practice.arena.command.ArenaCommands;
import me.ponktacology.practice.arena.listener.ArenaSelectionListener;
import me.ponktacology.practice.arena.match.MatchArena;
import me.ponktacology.practice.arena.match.MatchArenaCopy;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public class ArenaService extends Service {

  private final Map<String, Arena> arenas = Maps.newHashMap();
  private final ArenaRepository arenaRepository = new ArenaRepository();

  @Override
  public void configure() {
    addListener(new ArenaSelectionListener());
    addCommand(new ArenaCommands(this));

    // If arenas world doesn't exist - generate it
    if (Bukkit.getWorld("arenas") == null) {
      WorldCreator settings =
          new WorldCreator("arenas")
              .type(WorldType.FLAT)
              .generatorSettings("2;0;1;"); // This is what makes the world empty (void)

      Bukkit.createWorld(settings);
    }

    arenaRepository.loadAll().forEach(it -> arenas.put(it.getName().toUpperCase(Locale.ROOT), it));
  }

  @Override
  public void stop() {
    arenas.values().forEach(arenaRepository::save);
    arenas.clear();
  }

  public void createArena(Arena arena) {
    arenas.put(arena.getName().toUpperCase(Locale.ROOT), arena);
    arenaRepository.saveAsync(arena);
  }

  public void deleteArena(Arena arena) {
    arenas.remove(arena.getName().toUpperCase(Locale.ROOT));
    arenaRepository.deleteAsync(arena);
  }

  public Arena getArenaByName(String name) {
    return arenas.get(name.toUpperCase(Locale.ROOT));
  }

  public @Nullable MatchArenaCopy getRandomMatchArena() {
    MatchArena[] matchArenaList =
        arenas.values().stream()
            .filter(it -> it instanceof MatchArena)
            .map(it -> (MatchArena) it)
            .toArray(MatchArena[]::new);

    if (matchArenaList.length == 0) return null;

    MatchArena randomArena = matchArenaList[(int) (matchArenaList.length * Math.random())];

    return randomArena.getAvailableCopy();
  }
}
