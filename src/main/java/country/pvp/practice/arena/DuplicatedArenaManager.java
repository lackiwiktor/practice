package country.pvp.practice.arena;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class DuplicatedArenaManager {

    private final Map<Arena, Set<DuplicatedArena>> arenas = Maps.newConcurrentMap();

    public void add(Arena arena, Set<DuplicatedArena> duplicatedArenas) {
        arenas.put(arena, duplicatedArenas);
    }

    public void remove(Arena arena) {
        arenas.remove(arena);
    }

    public void addAll(Map<Arena, Set<DuplicatedArena>> copies) {
        arenas.putAll(copies);
    }

    public @Nullable DuplicatedArena getRandom() {
        Arena arena = arenas.keySet().toArray(new Arena[0])[(int) (arenas.size() * Math.random())];

        Set<DuplicatedArena> duplicatedArenas = arenas.get(arena);
        if (duplicatedArenas == null || duplicatedArenas.isEmpty()) return null;

        return duplicatedArenas
                .stream()
                .filter(it -> !it.isOccupied())
                .findFirst()
                .orElse(null);
    }
}
