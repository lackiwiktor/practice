package country.pvp.practice.ladder;

import com.google.common.collect.Maps;

import java.util.*;

public class LadderManager {

    private final Map<String, Ladder> ladders = Maps.newHashMap();

    public void addAll(Set<Ladder> ladders) {
        ladders.forEach(it -> this.ladders.put(it.getName().toUpperCase(Locale.ROOT), it));
    }

    public Ladder get(String name) {
        return ladders.get(name.toUpperCase(Locale.ROOT));
    }

    public void remove(Ladder ladder) {
        ladders.remove(ladder.getName().toUpperCase(Locale.ROOT));
    }

    public Set<Ladder> get() {
        return Collections.unmodifiableSet(new HashSet<>(ladders.values()));
    }
}
