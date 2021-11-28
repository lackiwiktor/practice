package country.pvp.practice.arena.duplicated;

import com.google.common.collect.Sets;
import country.pvp.practice.arena.Arena;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ArenaGenerator {

    public @NotNull Set<DuplicatedArena> generate(Arena parent, int amount, int offset) {
        Set<DuplicatedArena> duplicatedArenas = Sets.newHashSet();

        for (int index = 0; index < amount; index++) {
            DuplicatedArena arena = DuplicatedArena.from(parent, index * offset);
            duplicatedArenas.add(arena);
        }

        return duplicatedArenas;
    }
}
