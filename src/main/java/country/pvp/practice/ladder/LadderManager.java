package country.pvp.practice.ladder;

import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class LadderManager {

    private final Map<String, Ladder> ladders = Maps.newHashMap();

    public void add(@NotNull Ladder ladder) {
        this.ladders.put(ladder.getName().toUpperCase(Locale.ROOT), ladder);
    }

    public Ladder get(@NotNull String name) {
        return ladders.get(name.toUpperCase(Locale.ROOT));
    }

    public void remove(@NotNull Ladder ladder) {
        ladders.remove(ladder.getName().toUpperCase(Locale.ROOT));
    }

    public void addAll(@NotNull Set<Ladder> ladders) {
        ladders.forEach(this::add);
    }

    public @NotNull Set<Ladder> getAll() {
        return Collections.unmodifiableSet(ladders.values().stream().filter(Ladder::isSetup).collect(Collectors.toSet()));
    }

}
