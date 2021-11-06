package country.pvp.practice.arena;

import com.google.common.collect.Maps;

import java.util.*;

public class ArenaManager {

    private final Map<String, Arena> arenas = Maps.newHashMap();
    private final Map<Arena, Set<DuplicatedArena>> duplicatedArenas = Maps.newHashMap();

    public void add(Arena arena) {
        arenas.put(arena.getName().toUpperCase(Locale.ROOT), arena);
    }

    public void remove(Arena arena) {
        arenas.remove(arena.getName().toUpperCase(Locale.ROOT));
        duplicatedArenas.remove(arena);
    }

    public Arena get(String name) {
        return arenas.get(name.toUpperCase(Locale.ROOT));
    }

    public void addAll(Set<Arena> arenas) {
        arenas.forEach(this::add);
    }

    public void addDuplicatedArenas(Arena arena, Set<DuplicatedArena> arenas) {
        duplicatedArenas.put(arena, arenas);
    }

    public Set<DuplicatedArena> getDuplicatedArenas(Arena arena) {
        return duplicatedArenas.get(arena);
    }

    public Set<Arena> getAll() {
        return Collections.unmodifiableSet(new HashSet<>(arenas.values()));
    }

}
