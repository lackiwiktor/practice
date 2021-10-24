package country.pvp.practice.kit;

import com.google.common.collect.Maps;

import java.util.*;

public class KitManager {

    private final Map<String, Kit> kits = Maps.newHashMap();

    public void addAll(Set<Kit> kits) {
        kits.forEach(it -> this.kits.put(it.getName().toUpperCase(Locale.ROOT), it));
    }

    public Kit get(String name) {
        return kits.get(name.toUpperCase(Locale.ROOT));
    }

    public void remove(Kit kit) {
        kits.remove(kit.getName().toUpperCase(Locale.ROOT));
    }

    public Set<Kit> get() {
        return Collections.unmodifiableSet(new HashSet<>(kits.values()));
    }
}
