package country.pvp.practice.ladder;

import com.google.common.collect.Maps;

import java.util.*;

public class LadderManager {

    private final Map<String, Ladder> kits = Maps.newHashMap();

    public void addAll(Set<Ladder> ladders) {
        ladders.forEach(it -> this.kits.put(it.getName().toUpperCase(Locale.ROOT), it));
    }

    public Ladder get(String name) {
        return kits.get(name.toUpperCase(Locale.ROOT));
    }

    public void remove(Ladder ladder) {
        kits.remove(ladder.getName().toUpperCase(Locale.ROOT));
    }

    public Set<Ladder> get() {
        return Collections.unmodifiableSet(new HashSet<>(kits.values()));
    }
}
